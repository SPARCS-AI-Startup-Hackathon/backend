package com.backend.backend.member.service;

import com.backend.backend.config.security.SecurityUtil;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.dto.request.MemberRequest;
import com.backend.backend.member.exception.MemberDuplicate;
import com.backend.backend.member.mapper.MemberMapper;
import com.backend.backend.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public Long signIn(MemberRequest request) {
        if (memberRepository.findOneWithAuthoritiesByEmail(request.getEmail()).orElse(null) != null) {
            throw new MemberDuplicate();
        }

        Member member = memberMapper.toEntity(request);
        memberRepository.save(member);

        return member.getId();
    }

    public Member getMember() {
        Member member = SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return member;
    }
}
