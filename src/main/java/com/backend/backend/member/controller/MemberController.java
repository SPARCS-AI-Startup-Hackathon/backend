package com.backend.backend.member.controller;

import com.backend.backend.config.security.jwt.JwtFilter;
import com.backend.backend.config.security.jwt.TokenProvider;
import com.backend.backend.member.dto.request.MemberLoginRequest;
import com.backend.backend.member.dto.request.MemberRequest;
import com.backend.backend.member.dto.response.TokenResponseDto;
import com.backend.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
@Tag(name = "회원", description = "회원 API")
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate<String, String> redisTemplate;


    @PostMapping("sign-in")
    @Operation(summary = "회원가입", description = "회원가입을 한다.")

    public ResponseEntity<Long> signIn(@RequestBody @Valid MemberRequest request) {
        return ResponseEntity.ok(memberService.signIn(request));
    }

    @PostMapping("login")
    @Operation(summary = "로그인", description = "회원이 로그인을 한다.")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "성공"))
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody MemberLoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponseDto token = tokenProvider.createToken(authentication);
        String jwt = token.getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        redisTemplate.opsForValue().set(authentication.getName(), token.getRefreshToken(),
                token.getRefreshTokenValidationTime(), TimeUnit.MICROSECONDS);


        return ResponseEntity.ok().body(token);
    }
}
