这是一个基于netty的websocket项目,你可以直接使用他或者扩展他。
如果喜欢的话可以和我一起开发他。
加入我们或者其他问题都可以发送 给我的邮箱1003523958@qq.com
简单的介绍：
ws://localhost:8848/1 代表连接id为1的通信上线  你可以使用用户id替换他。
你可以发送join格式的字符串给websocket服务  他会把消息交给 id为"1","2","3","4","5","6","7" 的用户。
{"type":0,
  "send":["1","2","3","4","5","6","7"],
  "message":"2345867我是1告诉2345867是1告诉2345867我是1"
}

另外注意你需要定时发送心跳数据 1 
告诉服务你还在连接。
你可配置超时时间 默认是60秒
你可配置启动端口 默认是18181 
你可以配置webSocket的 服务端口 默认是8848

另外本项目已经支持上线时接收离线消息。如果你需要物理化离线的内容 请扩展项目。
如果喜欢请支持我。





This is a Netty based WebSocket project that you can use directly or extend.
You can develop him with me if you like.
If you want to join us or have other questions, please send them to my email address 1003523958@qq.com
Brief introduction:
Ws ://localhost:8848/1 stands for communication on-line with connection ID 1 and you can replace it with the user ID.
You can send a string in join format to the WebSocket service and it will send the message to the user id "1","2","3","4","5","6","7".
{" type ": 0.
"Send" : "1", "2", "3", "4", "5", "6", "7"].
"2345867 "" I'm 1" "Tell 2345867 "" I'm 1" "Tell 2345867"
}
Also note that you need to send heartbeat data 1 regularly
Tell the service you're still connected.
The default timeout for your configurable timeout is 60 seconds
Your configurable startup port defaults to 18181
You can configure webSocket's service port to be 8848 by default
In addition, this project has supported receiving offline messages when going online.If you need to physically take offline content please extend the project.
Please support me if you like.