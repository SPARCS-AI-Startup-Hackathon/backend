package com.backend.backend.ai.mapper;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.request.ClovaRequest;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClovaMapper {

    // 사용자의 응답 메시지 파싱
    public ClovaRequestList messageMapper(ChatMessage request) {
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest message = ClovaRequest.builder()
                .role("user")
                .content(request.getMessage())
                .build();

        requestList.add(message);

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
