package com.muxin.entry.netty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataContent implements Serializable {
	private static final long serialVersionUID = -7538381867533946883L;
	
	private Integer action;
	private ChatMsg chatMsg;
	private String extend;
}
