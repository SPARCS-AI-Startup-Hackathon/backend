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
                .content(Prompt.PROMPT_DATA + "추가로 이건 너(빛나래)와 사용자의 현재까지 대화내역이야\n" + history + "\n" + Prompt.UPDATE_PROMPT+Prompt.QUESTION_RULE)
                .build();
        requestList.add(promptUpdate);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList recommend(String history){
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest recommendDate = ClovaRequest.builder()
                .role("system")
                .content(Prompt.RECOMMEND_PROMPT_FIRST + history + Prompt.RECOMMEND_PROMPT_SECOND)
                .build();
        requestList.add(recommendDate);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList reRecommend(String history, List<String> recommended) {
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest reRecommendDate = ClovaRequest.builder()
                .role("system")
                .content(Prompt.RE_RECOMMEND_PROMPT_FIRST + recommended + Prompt.RE_RECOMMEND_PROMPT_SECOND + history +
                        Prompt.RE_RECOMMEND_PROMPT_THIRD)
                .build();
        requestList.add(reRecommendDate);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }

    public ClovaRequestList personalStatement(String history, String recommended) {
        List<ClovaRequest> requestList = new ArrayList<>();

        ClovaRequest personalStatementData = ClovaRequest.builder()
                .role("system")
                .content(Prompt.PERSONAL_STATEMENT_PROMPT_FIRST + history + Prompt.PERSONAL_STATEMENT_PROMPT_SECOND + recommended +
                        Prompt.PERSONAL_STATEMENT_PROMPT_TIRED)
                .build();

        requestList.add(personalStatementData);

        return ClovaRequestList.builder()
                .messages(requestList)
                .build();
    }
}
