package com.backend.backend.member.mapper;

import com.backend.backend.member.domain.Authority;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.dto.request.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {
    private final PasswordEncoder passwordEncoder;

    public Member toEntity(MemberRequest request) {
        Authority authority = Authority.builder().
                authorityName("ROLE_USER")
                .build();

        return Member.builder()
                .age(request.getAge())
                .tel(request.getTel())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .activated(true)
                .build();
    }

}
