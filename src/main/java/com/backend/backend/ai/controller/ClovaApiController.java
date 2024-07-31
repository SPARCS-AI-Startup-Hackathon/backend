package com.backend.backend.ai.controller;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.response.ChatList;
import com.backend.backend.ai.dto.response.JobRecommend;
import com.backend.backend.ai.dto.response.RecommendReason;
import com.backend.backend.ai.service.ClovaApiService;
import com.backend.backend.personalstatement.dto.response.PersonalStatementResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "클로바 대화API", description = "클로바를 사용해 사용자와 대화를 지원하는 API")
public class ClovaApiController {
    private final ClovaApiService clovaApiService;

    @GetMapping(value = "/first-question/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "첫 질문 생성", description = "사용자의 정보를 기반으로 첫 질문을 생성해주는 API")
    public Flux<String> firstQuestion(@PathVariable String token) throws Exception {
        return clovaApiService.firstSetting(token);
    }

    @PostMapping("/answer")
    @Operation(summary = "사용자의 답변", description = "사용자가 질문에 대한 답변을 한다")
    public void firstAnswer(@RequestBody @Valid ChatMessage message) {
        clovaApiService.getAnswer(message);
    }

    @GetMapping(value = "/question/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "질문 생성", description = "질문과 사용자의 답변을 기반으로 질문을 생성해주는 API")
    public Flux<String> buildQuestion(@PathVariable String token) throws Exception {
        return clovaApiService.callChatCompletionApi(token);
    }
    @GetMapping("/get-chat")
    @Operation(summary = "채팅 내역 보기", description = "AI와 주고받은 채팅내역을 조회한다")
    public ResponseEntity<ChatList> getChatHistory() {
        return ResponseEntity.ok(clovaApiService.getChatHistory());
    }

    @GetMapping("/recommend")
    @Operation(summary = "직업 추천 받기", description = "사용자가 지금까지 했던 대화내역을 기반으로 직업을 추천받는다")
    public ResponseEntity<JobRecommend> getRecommend() throws JsonProcessingException {
        return ResponseEntity.ok(clovaApiService.getRecommend());
    }

    @GetMapping("/recommend/reason")
    @Operation(summary = "직업 추천 이유", description = "사용자가 추천받은 이유를 응답한다.")
    public ResponseEntity<RecommendReason> getReason() throws JsonProcessingException {
        return ResponseEntity.ok(clovaApiService.getReason());
    }

    @GetMapping("/re-recommend")
    @Operation(summary = "직업 재추천 받기", description = "직업이 마음에 들지 않을시 재추천 받는다.")
    public ResponseEntity<JobRecommend> getReRecommend() throws JsonProcessingException {
        return ResponseEntity.ok().body(clovaApiService.getReRecommend());
    }

    @GetMapping("/personal-statement")
    @Operation(summary = "자기소개서 생성", description = "대화의 기록을 토대로 자기소개서를 받아온다.")
    public ResponseEntity<PersonalStatementResponse> buildPersonalStatement() throws JsonProcessingException {
        return ResponseEntity.ok(clovaApiService.buildPersonalStatement());
    }
}


