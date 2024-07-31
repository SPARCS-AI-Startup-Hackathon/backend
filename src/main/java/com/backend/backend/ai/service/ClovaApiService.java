package com.backend.backend.ai.service;

import com.backend.backend.ai.dto.request.ChatMessage;
import com.backend.backend.ai.dto.request.ClovaRequestList;
import com.backend.backend.ai.dto.response.ChatList;
import com.backend.backend.ai.dto.response.ChatResponse;
import com.backend.backend.ai.dto.response.JobRecommend;
import com.backend.backend.ai.dto.response.SttResponse;
import com.backend.backend.ai.mapper.ClovaMapper;
import com.backend.backend.config.security.jwt.TokenProvider;
import com.backend.backend.global.common.CommonUtil;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.repository.MemberRepository;
import com.backend.backend.member.service.MemberService;
import com.backend.backend.personalstatement.dto.response.PersonalStatementResponse;
import com.backend.backend.personalstatement.service.PersonalStatementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Objects;


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
    @Value("${spring.stt.api.client_id}")
    private String clientId;
    @Value("${spring.stt.api.client_secret}")
    private String clientSecret;
    private final ObjectMapper objectMapper;
    private final ClovaMapper clovaMapper;
    private final MemberService memberService;
    private final RedisTemplate<String, String> redisTemplate;
    private final WebClient clovaWebClient;
    private final WebClient sttWebClient;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PersonalStatementService personalStatementService;
    private final CommonUtil commonUtil;
    /**
     * 회원의 정보를 바탕으로 클라이언트측에 질문 하나 생성해서 던져주기
     */
    public Flux<String> firstSetting(String token) throws Exception {
        String email = tokenProvider.getEmailFromToken(token);
        Member member = memberRepository.findByEmail(email);
        ClovaRequestList clovaRequestList = clovaMapper.firstQuestion(member);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        return clovaWebClient.post()
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
                            log.info("RedisContentAI:{}", redisTemplate.opsForList().range("AI" + email, 0, -1));
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


    /**
     * 지금까지 했던 질문 + 답변 + 첫 프롬프팅을 다시 system에 넣어줘야함
     */
    public Flux<String> callChatCompletionApi(String token) throws JsonProcessingException {
        String email = tokenProvider.getEmailFromToken(token);

        ChatList chatHistory = getChatHistory(email);
        String history = objectMapper.writeValueAsString(chatHistory);

        ClovaRequestList clovaRequestList = clovaMapper.questionBuild(history);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        return clovaWebClient.post()
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
                            log.info("USER_ANSWER:{}",redisTemplate.opsForList().range("USER"+email, 0, -1));
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

    public SttResponse getTextByFile(MultipartFile file) {
        File convFile = null;
        try {
            Member member = memberService.getMember();
            String email = member.getEmail();
            String redisKey = "USER" + email;

            convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();

            String response = soundToText(convFile);
            SttResponse sttResponse = SttResponse.builder()
                    .text(response)
                    .build();

            redisTemplate.opsForList().rightPush(redisKey, sttResponse.getText());
            return sttResponse;
        } catch (Exception e) {
            throw new InvalidFileNameException("잘못된 파일", null);
        }finally {
            if (convFile != null && convFile.exists()) {
                convFile.delete();
            }
        }
    }
    public String soundToText(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String language = "Kor";

            Mono<String> responseMono = sttWebClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/recog/v1/stt")
                            .queryParam("lang", language)
                            .build())
                    .header("X-NCP-APIGW-API-KEY-ID", clientId)
                    .header("X-NCP-APIGW-API-KEY", clientSecret)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(fileContent)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::getTextFromResponse);

            return responseMono.block();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private String getTextFromResponse(String responseStr) {
        try {
            return objectMapper.readValue(responseStr, SttResponse.class).getText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatList getChatHistory() {
        Member member = memberService.getMember();
        String name = member.getName();
        ChatList chatList = new ChatList();
        String email = member.getEmail();

        List<String> aiQuestions = redisTemplate.opsForList().range("AI" + email, 0, -1);
        List<String> userAnswers = redisTemplate.opsForList().range("USER" + email, 0, -1);

        int size = Math.min(aiQuestions.size(), userAnswers.size());

        for (int i = 0; i < size; i++) {
            String aiQuestion = aiQuestions.get(i);
            String userAnswer = userAnswers.get(i);

            chatList.getChatResponses().add(ChatResponse.builder()
                    .sender("빛나래")
                    .content(aiQuestion)
                    .build());
            chatList.getChatResponses().add(ChatResponse.builder()
                    .sender(name)
                    .content(userAnswer)
                    .build());
        }
        // 질문의 수가 답변의 수보다 많다면 마지막 질문 추가
        if (aiQuestions.size() > userAnswers.size()) {
            chatList.getChatResponses().add(new ChatResponse("빛나래", aiQuestions.get(size)));
        }

        return chatList;
    }

    public ChatList getChatHistory(String email) {
        Member member = memberRepository.findByEmail(email);
        String name = member.getName();
        ChatList chatList = new ChatList();

        List<String> aiQuestions = redisTemplate.opsForList().range("AI" + email, 0, -1);
        List<String> userAnswers = redisTemplate.opsForList().range("USER" + email, 0, -1);

        int size = Math.min(aiQuestions.size(), userAnswers.size());

        for (int i = 0; i < size; i++) {
            String aiQuestion = aiQuestions.get(i);
            String userAnswer = userAnswers.get(i);

            chatList.getChatResponses().add(ChatResponse.builder()
                    .sender("빛나래")
                    .content(aiQuestion)
                    .build());
            chatList.getChatResponses().add(ChatResponse.builder()
                    .sender(name)
                    .content(userAnswer)
                    .build());
        }
        // 질문의 수가 답변의 수보다 많다면 마지막 질문 추가
        if (aiQuestions.size() > userAnswers.size()) {
            chatList.getChatResponses().add(new ChatResponse("빛나래", aiQuestions.get(size)));
        }

        return chatList;
    }

    public JobRecommend getRecommend() throws JsonProcessingException {
        String email = memberService.getMember().getEmail();

        ChatList chatHistory = getChatHistory();
        String history = objectMapper.writeValueAsString(chatHistory);

        ClovaRequestList clovaRequestList = clovaMapper.recommend(history);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl;
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        String responseBody = response.getBody();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        String content = rootNode.path("result").path("message").path("content").asText();

        redisTemplate.opsForList().rightPush("AI_RECOMMEND_JOB" + email, content);

        return JobRecommend.builder()
                .recommendJob(content)
                .build();
    }

    public JobRecommend getReRecommend() throws JsonProcessingException {
        String email = memberService.getMember().getEmail();

        ChatList chatHistory = getChatHistory();
        String history = objectMapper.writeValueAsString(chatHistory);

        List<String> recommended = redisTemplate.opsForList().range("AI_RECOMMEND_JOB" + email, 0, -1);

        ClovaRequestList clovaRequestList = clovaMapper.reRecommend(history,recommended);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl;
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        String content = rootNode.path("result").path("message").path("content").asText();

        redisTemplate.opsForList().rightPush("AI_RECOMMEND_JOB" + email, content);

        return JobRecommend.builder()
                .recommendJob(content)
                .build();
    }
    @Transactional
    public PersonalStatementResponse buildPersonalStatement() throws JsonProcessingException {
        Member member = memberService.getMember();
        String email = member.getEmail();

        ChatList chatHistory = getChatHistory(email);
        String history = objectMapper.writeValueAsString(chatHistory);
        String recommend = redisTemplate.opsForList().leftPop("AI_RECOMMEND_JOB" + email);

        ClovaRequestList clovaRequestList = clovaMapper.personalStatement(history, recommend, member);

        String body = objectMapper.writeValueAsString(clovaRequestList);

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl;
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();

        JsonNode rootNode = objectMapper.readTree(responseBody);
        String content = rootNode.path("result").path("message").path("content").asText();

        String format = formatContentToMarkdown(content);

        String markdown = commonUtil.markdown(format);

        String psContent = replaceString(markdown);

        PersonalStatementResponse personalStatementResponse = PersonalStatementResponse.builder()
                .job(recommend)
                .content(psContent)
                .name(member.getName())
                .build();

        personalStatementService.save(personalStatementResponse, member);

        return personalStatementResponse;
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-CLOVASTUDIO-API-KEY", apiKey);
        headers.set("X-NCP-APIGW-API-KEY", gatewayKey);

        return headers;
    }

    private String formatContentToMarkdown(String content) {
        return content
                .replace("\\n", "")
                .replaceAll(" +", " ")
                .replaceAll("(?m)^\\s+", "")
                .trim();
    }
    private String replaceString(String content) {
        return content.replace("\n", "");
    }
}

