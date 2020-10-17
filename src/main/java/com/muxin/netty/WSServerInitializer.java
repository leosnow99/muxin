package com.muxin.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitializer extends ChannelInitializer<SocketChannel> {
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		final ChannelPipeline pipeline = socketChannel.pipeline();
		
		//websocket 基于http协议, 需要有http编解码器;
		pipeline.addLast(new HttpServerCodec());
		//对写大数据流的支持
		pipeline.addLast(new ChunkedWriteHandler());
		//对httpMessage进行聚合, 聚合成FullHttpRequest或FullHttpResponse
		//几乎在netty编程中, 都会使用此handler
		pipeline.addLast(new HttpObjectAggregator(1024 * 64));
		//====================以上为了支持Http协议=============================
		
		//websocket服务器处理的协议, 用于指定给客户端连接访问的路由: /ws
		//此handler会版主你处理一i邪恶繁重复杂的事情
		//会帮你处理握手动作: handshaking(close, ping, pong) ping + pong = 心跳
		//对于websocket 来讲, 都是以frames进行传输的, 不同的数据累心对应的frames也不同
		pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
		
		//自定义handler
		pipeline.addLast(new ChatHandler());
	}
}
