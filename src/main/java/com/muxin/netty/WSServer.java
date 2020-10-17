package com.muxin.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WSServer {
	
	private final NioEventLoopGroup mainGroup;
	private final NioEventLoopGroup subGroup;
	private final ServerBootstrap server;
	private ChannelFuture future;
	
	
	private WSServer() {
		mainGroup = new NioEventLoopGroup();
		subGroup = new NioEventLoopGroup();
		server = new ServerBootstrap();
		
		server.group(mainGroup, subGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new WSServerInitializer());
	}
	
	private static class SingletonWSServer {
		static final WSServer instance = new WSServer();
	}
	
	public static WSServer getInstance() {
		return SingletonWSServer.instance;
	}
	
	public void start() {
		this.future = server.bind(8088);
		System.out.println("[info]=======ServerBootStrap启动成功!");
	}
	
	
}
