package com.backend.backend.ai.mapper;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.request.ClovaRequest;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import com.backend.backend.global.ai.Prompt;
import com.backend.backend.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClovaMapper {
    private final RedisTemplate<String, String> redisTemplate;

    // 첫번째 질문 파싱
    public ClovaRequestList firstQuestion(Member member) {
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest clovaRequest = ClovaRequest.builder()
                .role("system")
                .content(Prompt.PROMPT_DATA + "나이:" + member.getAge() + "이름:" + member.getName()
                        + member.getName() + "님의 특징:" + member.getIntroduction())
                .build();

        requestList.add(clovaRequest);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList messageMapper(ChatMessage request, Member member) {
        List<ClovaRequest> requestList = new ArrayList<>();
//        // 현재까지의 질문 및 답변을 넣어줘야 함.
//        ClovaRequest.builder()
//                .role("system")
//                .content(QUESTIONS + )
//                .build();

        ClovaRequest userMessage = ClovaRequest.builder()
                .role("user")
                .content(request.getMessage())
                .build();

        requestList.add(userMessage);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    // TODO 기존 채팅내역 프롬프팅

//    public ClovaRequestList listMapper(List<ClovaRequest> request) {
//        return ClovaRequestList.builder()
//                .messages(request)
//                .build();
//    }
}
