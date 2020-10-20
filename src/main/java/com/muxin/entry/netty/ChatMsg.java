package com.muxin.entry.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsg implements Serializable {
	private static final long serialVersionUID = -7127997149160396158L;
	
	//聊天内容
	private String msg;
	private String sendId;
	private String receiverId;
	//用于消息签收
	private String msgId;
	

}
