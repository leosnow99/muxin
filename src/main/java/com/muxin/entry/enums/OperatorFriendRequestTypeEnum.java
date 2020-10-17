package com.muxin.entry.enums;

public enum OperatorFriendRequestTypeEnum {
	IGNORE(0, "忽略"),
	PASS(1, "通过");
	
	public final Integer type;
	public final String msg;
	
	OperatorFriendRequestTypeEnum(Integer type, String msg) {
		this.type = type;
		this.msg = msg;
	}
	
	public static OperatorFriendRequestTypeEnum getByType(Integer type) {
		switch (type) {
			case 0:
				return IGNORE;
			case 1:
				return PASS;
		}
		return null;
	}
}
