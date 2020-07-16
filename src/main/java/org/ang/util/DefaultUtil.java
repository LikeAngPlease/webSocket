package org.ang.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class DefaultUtil {
    public static DefaultFullHttpResponse stringToDefaultFullHttpResponse(String str) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(str, CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            response.headers().set("Content-Length", response.content().readableBytes());
        }
        return response;
    }
}
