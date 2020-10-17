package com.muxin.entry.enums;

public enum SearchFriendsStatusEnum {
	SUCCESS(0, "OK"),
	USER_NOT_EXIST(1, "无此用户"),
	NOT_YOURSELF(2, "不能添加你自己"),
	ALREADY_FRIENDS(3, "该用户已经是你好友");
	
	public final Integer status;
	
	public String msg;
	
	SearchFriendsStatusEnum(Integer status, String msg) {
		this.status = status;
		this.msg = msg;
	}
}
