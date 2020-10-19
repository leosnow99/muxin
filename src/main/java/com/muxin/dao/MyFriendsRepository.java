package com.muxin.dao;

import com.muxin.entry.MyFriends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MyFriendsRepository extends JpaRepository<MyFriends, String>, JpaSpecificationExecutor<MyFriendsRepository> {
	MyFriends findByMyUserIdAndMyFriendId(String myUserId, String myFriendId);
	
}
