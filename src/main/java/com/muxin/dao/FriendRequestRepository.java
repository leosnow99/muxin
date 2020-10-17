package com.muxin.dao;


import com.muxin.entry.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, String>, JpaSpecificationExecutor<String> {
	FriendRequest findBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);
	@Transactional
	void deleteBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);
}
