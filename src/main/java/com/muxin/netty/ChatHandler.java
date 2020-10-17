package com.muxin.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

//TextWebSocketFrame: 在netty中, 是用于为websocket 专门处理文本的对象, frame是消息到载体
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	//用于记录和管理所有channel
	private static final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
		String content = msg.text();
		System.out.println("接收到的数据: " + content);
		
		for (Channel client : clients) {
			client.writeAndFlush(new TextWebSocketFrame("[服务器接收到消息: ]" + LocalDateTime.now() + ", 消息为" + content));
		}
	}
	
	//当客户端连接服务器端之后(打开链接)
	//获取客户端的channel, 并且
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		clients.add(ctx.channel());
	}
	
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved, ChannelGroup会自动溢出对应客户端的channel
		//clients.remove(ctx.channel());
		System.out.println(ctx.channel().id().asShortText());
	}
}
