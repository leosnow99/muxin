package com.muxin.entry;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "users")
public class Users {
	@Id
	private String id;
	
	private String username;
	private String password;
	private String faceImage;
	private String faceImageBig;
	private String nickname;
	private String qrcode;
	private String cid;
}
