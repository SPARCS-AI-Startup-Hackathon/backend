package com.backend.backend.ai.service;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import com.backend.backend.ai.mapper.ClovaMapper;
import com.backend.backend.ai.repository.ClovaRepository;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;



@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaApiService {
    private final ObjectMapper objectMapper;
    private final ClovaMapper clovaMapper;
    private final MemberService memberService;
    private final ClovaRepository clovaRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.clova.api.url}")
    private String apiUrl;
    @Value("${spring.clova.api.api_key}")
    private String apiKey;
    @Value("${spring.clova.api.api_gateway_key}")
    private String gatewayKey;

    // 회원이 보낸 데이터 -> userContent, Ai가 보낸 데이터 -> Ai Content. 두개 합쳐서 Role: System으로 미리 프롬프팅해서 보내야됨

    /**
     *
     */
    @Transactional
    public String callChatCompletionApi(ChatMessage request) throws JsonProcessingException {
        Member member = memberService.getMember();

        // 사용자가 질문을 처음 든는경우
        if (clovaRepository.findByMember(member).isEmpty()) {

        }
        ClovaRequestList clovaRequestList = clovaMapper.messageMapper(request);

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl;
        HttpHeaders headers = buildHeaders();
        String body = objectMapper.writeValueAsString(clovaRequestList);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        log.info("header: {}", entity.getHeaders());
        log.info("body: {}", entity.getBody());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "text/event-stream");
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
        headers.set("X-NCP-APIGW-API-KEY", gatewayKey);

        return headers;
    }
}