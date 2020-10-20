package com.muxin.entry.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

//用户id和channel的关联关系处理
public class UserChannelRel {
	private static final HashMap<String, Channel> manager = new HashMap<>();
	
	public static void put(String sendId, Channel channel) {
		manager.put(sendId, channel);
	}
	
	public static Channel get(String sendId) {
		return manager.get(sendId);
	}
	
	public static void output() {
		for (Map.Entry<String, Channel> entry : manager.entrySet()) {
			System.out.println("UserId: " + entry.getKey() + "  ChannelId: " + entry.getValue().id());
		}
	}
}
