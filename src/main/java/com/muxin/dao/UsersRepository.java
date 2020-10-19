package com.muxin.dao;

import com.muxin.entry.Users;
import com.muxin.entry.vo.FriendRequestVO;
import com.muxin.entry.vo.FriendsVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, String>, JpaSpecificationExecutor<Users> {
	Users findByUsername(String username);
	
	Users findByUsernameAndPassword(String username, String password);
	
	@Query(value = "select sender.id as sendUserId, sender.username as sendUsername, sender.face_image as sendFaceImage, sender.nickname as sendNickName from friend_request fr left join users sender on fr.send_user_id = sender.id where fr.accept_user_id = ?1", nativeQuery = true)
	List<FriendRequestVO> queryFriendRequestList(String userId);
	
	@Query(value = "select users.id as userId, users.username as userName, users.nickname as nickName, users.face_image as faceImage from my_friends mf left join users on users.id = mf.my_friend_id where mf.my_user_id = ?1", nativeQuery = true)
	List<FriendsVO> queryFriendsByUserId(String userId);
}
