package com.muxin.service.impl;

import com.muxin.dao.FriendRequestRepository;
import com.muxin.dao.MyFriendsRepository;
import com.muxin.dao.UsersRepository;
import com.muxin.entry.FastDFSFile;
import com.muxin.entry.FriendRequest;
import com.muxin.entry.MyFriends;
import com.muxin.entry.Users;
import com.muxin.entry.enums.OperatorFriendRequestTypeEnum;
import com.muxin.entry.enums.SearchFriendsStatusEnum;
import com.muxin.entry.vo.FriendRequestVO;
import com.muxin.service.UserService;
import com.muxin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private MyFriendsRepository myFriendsRepository;
	
	@Autowired
	private FriendRequestRepository friendRequestRepository;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private QRCodeUtils qrCodeUtils;
	
	public Users getById(String id) {
		final Optional<Users> optional = usersRepository.findById(id);
		Users user = null;
		if (optional.isPresent()) {
			user = optional.get();
		}
		return user;
	}
	
	@Override
	public boolean queryUsernameIsExist(String username) {
		final Users user = usersRepository.findByUsername(username);
		return user != null;
	}
	
	@Override
	public Users register(Users user) {
		try {
			user.setId(String.valueOf(idWorker.nextId()));
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFaceImageBig("");
			user.setFaceImage("");
			user.setQrcode(createQrCodePath(user.getId(), user.getUsername()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		usersRepository.save(user);
		
		return user;
	}
	
	@Override
	public Users uploadFace(String userId, String faceDate) {
		try {
			//更新用户对象
			final Users user = getById(userId);
			if (user == null) {
				return null;
			}
			
			String userFacePath = "./cache/" + userId + "userFace64.png";
			final boolean flag;
			flag = FileUtils.base64ToFile(userFacePath, faceDate);
			if (flag) {
				FastDFSFile fastDFSFile = new FastDFSFile(
						userFacePath,
						File2byte(new File(userFacePath)),
						"png"
				);
				//文件上传
				String[] upload = FastDFSClient.upload(fastDFSFile);
				//组装文件上传地址
				final String fileURL = FastDFSClient.getTrackerUrl() + "/" + upload[0] + "/" + upload[1];
				
				user.setId(userId);
				user.setFaceImage(fileURL);
				user.setFaceImageBig(fileURL);
				updateUserInfo(user);
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public SearchFriendsStatusEnum preconditionSearchFriends(String myUserId, String friendUsername) {
		//搜索用户是否存在
		final Users user = usersRepository.findByUsername(friendUsername);
		if (user == null) {
			return SearchFriendsStatusEnum.USER_NOT_EXIST;
		}
		//搜索账户是否为本身
		if (user.getId().equals(myUserId)) {
			return SearchFriendsStatusEnum.NOT_YOURSELF;
		}
		//搜索账户是否已经是好友
		final MyFriends myFriends = myFriendsRepository.findByMyUserIdAndMyFriendId(myUserId, user.getId());
		if (myFriends != null) {
			return SearchFriendsStatusEnum.ALREADY_FRIENDS;
		}
		SearchFriendsStatusEnum.SUCCESS.msg = user.getId();
		return SearchFriendsStatusEnum.SUCCESS;
	}
	
	//添加好友请求记录
	@Override
	public void sendFriendRequest(String myUserId, String friendUsername) {
		//查询朋友信息
		final Users friendUser = usersRepository.findByUsername(friendUsername);
		//查询发送好友请求记录
		final FriendRequest friendRequest = friendRequestRepository.findBySendUserIdAndAcceptUserId(myUserId, friendUser.getId());
		if (friendRequest == null) {
			//不是你的好友, 没有发送过请求
			final FriendRequest request = new FriendRequest();
			request.setId(String.valueOf(idWorker.nextId()));
			request.setSendUserId(myUserId);
			request.setAcceptUserId(friendUser.getId());
			request.setRequestDataTime(new Date());
			
			friendRequestRepository.save(request);
		}
	}
	
	@Override
	public Users findByUsername(String username) {
		return usersRepository.findByUsername(username);
	}
	
	@Override
	public List<FriendRequestVO> queryFriendRequests(String userId) {
		return usersRepository.queryFriendRequestList(userId);
	}
	
	@Override
	public void operatorFriendRequest(String sendUserId, String acceptUserId, OperatorFriendRequestTypeEnum typeEnum) {
		if (typeEnum == OperatorFriendRequestTypeEnum.IGNORE) {
			//忽略好友请求
			deleteFriendRequest(sendUserId, acceptUserId);
		} else if (typeEnum == OperatorFriendRequestTypeEnum.PASS) {
			//通过好友请求
			passFriendRequest(sendUserId, acceptUserId);
		}
	}
	
	//通过好友请求
	private void passFriendRequest(String sendUserId, String acceptUserId) {
		//保存好友
		final MyFriends friends = new MyFriends();
		friends.setId(String.valueOf(idWorker.nextId()));
		friends.setMyUserId(acceptUserId);
		friends.setMyFriendId(sendUserId);
		
		myFriendsRepository.save(friends);
		//逆向保存好友
		friends.setId(String.valueOf(idWorker.nextId()));
		friends.setMyUserId(sendUserId);
		friends.setMyFriendId(acceptUserId);
		
		//删除好友记录
		deleteFriendRequest(sendUserId, acceptUserId);
	}
	
	//删除好友请求
	public void deleteFriendRequest(String sendUserId, String acceptUserId) {
		friendRequestRepository.deleteBySendUserIdAndAcceptUserId(sendUserId, acceptUserId);
	}
	
	private String createQrCodePath(String userId, String username) {
		String qrCodePath = "./cache/" + userId + "qrcode.png";
		qrCodeUtils.createQRCode(qrCodePath, "qrcode:" + username);
		FastDFSFile fastDFSFile = new FastDFSFile(
				qrCodePath,
				File2byte(new File(qrCodePath)),
				"png"
		);
		//文件上传
		String[] upload = FastDFSClient.upload(fastDFSFile);
		//组装文件上传地址
		return FastDFSClient.getTrackerUrl() + "/" + upload[0] + "/" + upload[1];
	}
	
	@Override
	public Users queryUserForLogin(String username, String pwd) {
		try {
			return usersRepository.findByUsernameAndPassword(username, MD5Utils.getMD5Str(pwd));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void updateUserInfo(Users users) {
		if (users.getId() == null) {
			return;
		}
		usersRepository.save(users);
	}
	
	public static byte[] File2byte(File tradeFile) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(tradeFile);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
}
