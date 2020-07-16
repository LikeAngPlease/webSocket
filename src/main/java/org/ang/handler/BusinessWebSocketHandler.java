package org.ang.handler;


import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.ang.pool.Channelpool;
import org.ang.service.MessageParsingService;
import org.ang.util.DefaultUtil;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class BusinessWebSocketHandler extends SimpleChannelInboundHandler {

    private WebSocketServerHandshaker handshaker;

    private static String webSocketUrl;

    public static void setWebSocketUrl(String webSocketUrl) {
        webSocketUrl = webSocketUrl;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 客户端第一次接入，升级Upgrade  websocket
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        // websocket数据交互
        else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.toString());
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 当前需求仅需要文本消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supportes", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        MessageParsingService.messageParsing(request, this);
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ctx.channel().write(new TextWebSocketFrame(timeStr + ": OK"));
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果http解析失败，返回异常
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req);
            return;
        }
        // 构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://127.0.0.1:8848", null, false);
        handshaker = wsFactory.newHandshaker(req);
        Channel channel = ctx.channel();
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channel);
        } else {
            handshaker.handshake(ctx.channel(), req);
            //获取唯一id 和 Channel 保存下来
            String id = req.uri().substring(1);
            Channelpool.setChannel(id, channel);
            //查看是否有延时消息
            LinkedList linkedList = Channelpool.getDelayLinkedList(id);
            if (linkedList == null || linkedList.size() == 0) {

            } else {
                ctx.channel().write(new TextWebSocketFrame(linkedList.toString()));
            }
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 返回给客户端
//        if(response.status().code() != 200){
//            ByteBuf buf = Unpooled.copiedBuffer(req.toString().toString(), CharsetUtil.UTF_8);
//            response.content().writeBytes(buf);
//            buf.release();
//            response.headers().set("Content-Length",response.content().readableBytes());
//        }
        String message = req.toString();
        ChannelFuture f = ctx.channel().writeAndFlush(DefaultUtil.stringToDefaultFullHttpResponse(req.toString()));
        // 如果是非Keep-Alive,关闭连接
        if (!req.headers().get("Connection").equals("keep-alive")) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 处理超时事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent event = (IdleStateEvent) evt;

        switch (event.state()) {
            case READER_IDLE:
                ctx.close();
                break;
            case WRITER_IDLE:
                break;
            case ALL_IDLE:
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 未完成
     *
     * @param message
     * @param ids
     * @return
     */
    public boolean sendToResponse(String message, List<String> ids) {
        ids.forEach((id) -> {
                    Channel channel = Channelpool.getChannel(id);
                    if (channel == null) {

                    } else {
                        channel.writeAndFlush(DefaultUtil.stringToDefaultFullHttpResponse(message));
                    }
                }
        );
        return true;
    }

    /**
     * 发送消息给客户端
     *
     * @param message
     * @param ids
     * @return
     */
    public boolean sendToTextWebSocketFrame(String message, List<String> ids) {
        ids.forEach((id) -> {
                    Channel channel = Channelpool.getChannel(id);
                    if (channel == null || !channel.isActive()) {
                        Channelpool.deleteChannel(id);
                        Channelpool.delayMessage(id, message);
                    } else {
                        channel.writeAndFlush(new TextWebSocketFrame(message));
                    }
                }
        );
        return true;
    }
}
