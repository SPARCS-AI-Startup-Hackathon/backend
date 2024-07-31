package com.backend.backend.ai.mapper;

import com.backend.backend.ai.dto.request.ClovaRequest;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import com.backend.backend.global.ai.Prompt;
import com.backend.backend.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ClovaMapper {

    public ClovaRequestList firstQuestion(Member member) {
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest clovaRequest = ClovaRequest.builder()
                .role("system")
                .content(Prompt.PROMPT_DATA + "나이:" + member.getAge() + "이름:" + member.getName()
                        + member.getName() + "님의 특징:" + member.getIntroduction() +Prompt.FIRST_QUESTION)
                .build();

        requestList.add(clovaRequest);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList questionBuild(String history){
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest promptUpdate = ClovaRequest.builder()
                .role("system")
                .content(Prompt.PROMPT_DATA + "추가로 이건 현재까지 대화내역이야\n" + history + "\n" + Prompt.UPDATE_PROMPT)
                .build();
        requestList.add(promptUpdate);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList recommend(String history){
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest promptUpdate = ClovaRequest.builder()
                .role("system")
                .content(Prompt.RECOMMEND_PROMPT_FIRST + history + Prompt.RECOMMEND_PROMPT_SECOND)
                .build();
        requestList.add(promptUpdate);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }
}
