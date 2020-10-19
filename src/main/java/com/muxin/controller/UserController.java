package com.muxin.controller;

import com.muxin.entry.Users;
import com.muxin.entry.bo.UsersBO;
import com.muxin.entry.enums.OperatorFriendRequestTypeEnum;
import com.muxin.entry.enums.SearchFriendsStatusEnum;
import com.muxin.entry.vo.FriendRequestVO;
import com.muxin.entry.vo.UsersVo;
import com.muxin.service.UserService;
import com.muxin.utils.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/{id}")
	public Users getById(@PathVariable String id) {
		return userService.getById(id);
	}
	
	@PostMapping("/registOrLogin")
	public Result registerOrLogin(@RequestBody Users users) {
		//判断用户名和密码不能为空
		if (StringUtils.isEmpty(users.getUsername()) || StringUtils.isEmpty(users.getPassword())) {
			return Result.errorMsg("用户名和密码不能为空!");
		}
		
		final boolean usernameIsExist = userService.queryUsernameIsExist(users.getUsername());
		Users userResult = null;
		if (usernameIsExist) {
			//登录
			userResult = userService.queryUserForLogin(users.getUsername(), users.getPassword());
			if (userResult == null) {
				return Result.errorMsg("用户名或密码不正确!");
			}
		} else {
			//注册
			userResult = userService.register(users);
		}
		final UsersVo usersVo = new UsersVo();
		BeanUtils.copyProperties(userResult, usersVo);
		
		return Result.ok(usersVo);
	}
	
	@PostMapping("/uploadFaceBase64")
	public Result uploadFaceBase64(@RequestBody UsersBO usersBO) {
		//获取前端传递的base64字符串, 转换为文件对象
		try {
			final String faceDate = usersBO.getFaceData();
			if (StringUtils.isEmpty(faceDate) || StringUtils.isEmpty(usersBO.getUserId())) {
				return Result.errorMsg("无数据!");
			}
			
			final Users users = userService.uploadFace(usersBO.getUserId(), usersBO.getFaceData());
			if (users != null) {
				return Result.ok(users);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.errorMsg("上传失败!");
	}
	
	@PostMapping("/editNickname")
	public Result editNickName(@RequestBody UsersBO usersBO) {
		final Users user = userService.getById(usersBO.getUserId());
		if (user != null && !StringUtils.isEmpty(usersBO.getNickname())) {
			user.setNickname(usersBO.getNickname());
			userService.updateUserInfo(user);
			return Result.ok(user);
		}
		return Result.errorMsg("更新失败");
	}
	
	//搜索好友接口, 根据账号做匹配查询
	@PostMapping("/search")
	public Result searchUser(String myUserId, String friendUsername) {
		if (StringUtils.isEmpty(myUserId) || StringUtils.isEmpty(friendUsername)) {
			return Result.errorMsg("参数错误");
		}
		
		final SearchFriendsStatusEnum searchFriendsStatusEnum = userService.preconditionSearchFriends(myUserId, friendUsername);
		if (searchFriendsStatusEnum == SearchFriendsStatusEnum.SUCCESS) {
			final Users user = userService.getById(searchFriendsStatusEnum.msg);
			final UsersVo result = new UsersVo();
			BeanUtils.copyProperties(user, result);
			return Result.ok(result);
		}
		return Result.errorMsg(searchFriendsStatusEnum.msg);
	}
	
	//发送请求消息
	@PostMapping("/addFriendRequest")
	public Result addFriendRequest(String myUserId, String friendUsername) {
		if (StringUtils.isEmpty(myUserId) || StringUtils.isEmpty(friendUsername)) {
			return Result.errorMsg("参数错误");
		}
		
		final SearchFriendsStatusEnum searchFriendsStatusEnum = userService.preconditionSearchFriends(myUserId, friendUsername);
		if (searchFriendsStatusEnum == SearchFriendsStatusEnum.SUCCESS) {
			userService.sendFriendRequest(myUserId, friendUsername);
			return Result.ok();
		}
		return Result.errorMsg(searchFriendsStatusEnum.msg);
	}
	
	//获取好友请求列表
	@PostMapping("/queryFriendRequests")
	public Result queryFriendRequests(String userId) {
		final List<FriendRequestVO> result = userService.queryFriendRequests(userId);
		return Result.ok(result);
	}
	
	//处理好友请求
	@PostMapping("/operFriendRequest")
	public Result operFriendRequest(String acceptUserId, String sendUserId, Integer operType) {
		if (StringUtils.isEmpty(acceptUserId) || StringUtils.isEmpty(sendUserId) || operType == null) {
			return Result.errorMsg("参数错误!");
		}
		final OperatorFriendRequestTypeEnum type = OperatorFriendRequestTypeEnum.getByType(operType);
		if (type == null) {
			return Result.errorMsg("参数错误!");
		}
		userService.operatorFriendRequest(sendUserId, acceptUserId, type);
		return Result.ok(userService.queryMyFriends(acceptUserId));
	}
	
	@GetMapping("/myFriends")
	public Result myFriends(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return Result.errorMsg("用户名不能为空!");
		}
		return Result.ok(userService.queryMyFriends(userId));
	}
}
