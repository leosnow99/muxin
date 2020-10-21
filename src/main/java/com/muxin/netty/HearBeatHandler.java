package com.muxin.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

//继承ChannelInboundHandlerAdapter 从而不需要实现channelRead0方法
//哟ing与检测channel的心跳handler
public class HearBeatHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		//判断evt是否是IdleStateEvent(用于触发用户时间, 包换读空闲/写空闲/读写空闲)
		if (evt instanceof IdleStateEvent) {
			final IdleStateEvent event = (IdleStateEvent) evt;
			//读请求
			if (event.state() == IdleState.READER_IDLE) {
				System.out.println("进入读空闲");
			} else if (event.state() == IdleState.WRITER_IDLE) {
				System.out.println("进入写空闲");
			} else if (event.state() == IdleState.ALL_IDLE) {
				final Channel channel = ctx.channel();
				//关闭无用channel,防止资源浪费
				System.out.println("channel关闭前, users的数量为: " + ChatHandler.users.size());
				channel.close();
				System.out.println("channel关闭后, users的数量为: " + ChatHandler.users.size());
			}
		}
	}
}
