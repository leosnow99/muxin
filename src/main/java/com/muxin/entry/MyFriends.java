package com.muxin.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "my_friends")
public class MyFriends {
	@Id
	private String id;
	private String myUserId;
	private String myFriendId;
}
