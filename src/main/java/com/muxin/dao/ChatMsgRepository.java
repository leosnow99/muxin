package com.muxin.dao;

import com.muxin.entry.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMsgRepository extends JpaRepository<ChatMsg, String>, JpaSpecificationExecutor<String> {
	@Transactional
	@Modifying
	@Query(value = "update ChatMsg cm set cm.signFlag = 1 where cm.id in(?1)")
	void updateMsgSigned(List msgIds);
	
	List<ChatMsg> findAllByAcceptUserIdAndSignFlag(String acceptUserId, Integer flag);
}
