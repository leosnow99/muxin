package com.muxin.service;

import com.muxin.entry.FriendRequest;
import com.muxin.entry.Users;
import com.muxin.entry.enums.OperatorFriendRequestTypeEnum;
import com.muxin.entry.enums.SearchFriendsStatusEnum;
import com.muxin.entry.vo.FriendRequestVO;
import com.muxin.entry.vo.UsersVo;

import java.util.List;

public interface UserService {
	
	Users getById(String id);
	
	boolean queryUsernameIsExist(String username);
	
	//注册用户
	Users register(Users users);
	
	//查询用户是否存在
	Users queryUserForLogin(String username, String pwd);
	
	//修改用户记录
	void updateUserInfo(Users users);
	
	Users uploadFace(String userId, String faceDate);
	
	SearchFriendsStatusEnum preconditionSearchFriends(String myUserId, String friendUsername);
	
	//添加好友请求记录
	void sendFriendRequest(String myUserId, String friendUsername);
	
	//根据用户名查询用户信息
	Users findByUsername(String username);
	
	//获取好友请求列表
	List<FriendRequestVO> queryFriendRequests(String userId);
	
	//处理好友请求
	void operatorFriendRequest(String sendUserId, String acceptUserId, OperatorFriendRequestTypeEnum typeEnum);
	
}