package com.muxin.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "friend_request")
public class FriendRequest {
	@Id
	private String id;
	private String sendUserId;
	private String acceptUserId;
	private Date requestDataTime;
	
}
