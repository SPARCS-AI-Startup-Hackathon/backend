package com.backend.backend.ai.repository;

import com.backend.backend.ai.domain.AiChat;
import com.backend.backend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClovaRepository extends JpaRepository<AiChat, Long> {
    Optional<AiChat> findByMember(Member member);
}
