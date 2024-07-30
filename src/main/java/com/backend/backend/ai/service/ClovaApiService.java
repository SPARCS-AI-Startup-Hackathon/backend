package com.backend.backend.ai.service;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import com.backend.backend.ai.mapper.ClovaMapper;
import com.backend.backend.config.security.jwt.TokenProvider;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.repository.MemberRepository;
import com.backend.backend.member.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class ClovaApiService {
    @Value("${spring.clova.api.url}")
    private String apiUrl;
    @Value("${spring.clova.api.api_key}")
    private String apiKey;
    @Value("${spring.clova.api.api_gateway_key}")
    private String gatewayKey;
    private final ObjectMapper objectMapper;
    private final ClovaMapper clovaMapper;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;


    /**
     * 회원의 정보를 바탕으로 클라이언트측에 질문 하나 생성해서 던져주기
     */
    public Flux<String> firstSetting(String token) throws Exception {
        String email = tokenProvider.getEmailFromToken(token);
        Member member = memberRepository.findByEmail(email);
        ClovaRequestList clovaRequestList = clovaMapper.firstQuestion(member);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        return webClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .header("X-NCP-CLOVASTUDIO-API-KEY", apiKey)
                .header("X-NCP-APIGW-API-KEY", gatewayKey)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .delayElements(Duration.ofMillis(200))
                .doOnNext(response -> {
                    log.info("RESPONSE: {}", response);
                    if (response.contains("seed")) {
                        try {
                            JsonNode jsonNode = objectMapper.readTree(response);
                            JsonNode messageNode = jsonNode.get("message");
                            JsonNode contentNode = messageNode.get("content");
                            String content = contentNode.asText();
                            log.info("최종컨텐츠:{}", content);
                            redisTemplate.opsForList().rightPush("AI" + email, content);
                            log.info("RedisContentAI:{}",redisTemplate.opsForList().range("AI" + email, 0, -1));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_COMPLETE) {
                        log.info("Streaming completed.");
                    }
                });
    }


    public void getFirstAnswer(ChatMessage message) {
        Member member = memberService.getMember();
        String email = member.getEmail();

        String redisKey = "USER" + email;
        // 회원 첫번째 답변 받아서 Redis 저장
        redisTemplate.opsForList().rightPush(redisKey, message.getMessage());
        log.info("AI_QUESTION:{}", redisTemplate.opsForList().range("AI" + email, 0, -1));
        log.info("USER_ANSWER:{}", redisTemplate.opsForList().range(redisKey, 0, -1));
    }


    @Transactional
    public String callChatCompletionApi(ChatMessage request) throws JsonProcessingException {
        Member member = memberService.getMember();

        ClovaRequestList clovaRequestList = clovaMapper.messageMapper(request, member);

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl;
        HttpHeaders headers = buildHeaders();
        String body = objectMapper.writeValueAsString(clovaRequestList);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);


        log.info("header: {}", entity.getHeaders());
        log.info("body: {}", entity.getBody());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String data = redisTemplate.opsForValue().get(member.getEmail());
        // 사용자 답변 내용 저장
        redisTemplate.opsForValue().set(member.getEmail(), request.getMessage());
        // AI 다음 질문(답변) 저장

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
