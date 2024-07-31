package com.backend.backend.personalstatement.service;

import com.backend.backend.global.exception.InvalidRequest;
import com.backend.backend.member.domain.Member;
import com.backend.backend.member.service.MemberService;
import com.backend.backend.personalstatement.domain.PersonalStatement;
import com.backend.backend.personalstatement.dto.request.PersonalStatementUpdate;
import com.backend.backend.personalstatement.dto.response.PersonalStatementResponse;
import com.backend.backend.personalstatement.mapper.PersonalStatementMapper;
import com.backend.backend.personalstatement.repository.PersonalStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalStatementService {
    private final MemberService memberService;
    private final PersonalStatementRepository personalStatementRepository;
    private final PersonalStatementMapper personalStatementMapper;
    @Transactional
    public void save(PersonalStatementResponse request, Member member) {
        PersonalStatement personalStatement = PersonalStatement.builder()
                .name(request.getName())
                .job(request.getJob())
                .member(member)
                .content(request.getContent())
                .build();

        personalStatementRepository.save(personalStatement);
    }

    public List<PersonalStatementResponse> getMembersPs() {
        Member member = memberService.getMember();

        List<PersonalStatement> psList = personalStatementRepository.findAllByMember(member);
        return psList.stream().map(personalStatementMapper::toResponse).collect(Collectors.toList());
    }

    public PersonalStatementResponse getMemberPs(Long id) {
        PersonalStatement personalStatement = personalStatementRepository.findById(id)
                .orElseThrow(InvalidRequest::new);

        return personalStatementMapper.toResponse(personalStatement);
    }

    @Transactional
    public PersonalStatementResponse update(PersonalStatementUpdate request, Long id) {
        PersonalStatement personalStatement = personalStatementRepository.findById(id)
                .orElseThrow(InvalidRequest::new);

        personalStatement.update(request.getContent());

        return personalStatementMapper.toResponse(personalStatement);
    }

    public void delete(Long id) {
        personalStatementRepository.delete(personalStatementRepository.findById(id)
                .orElseThrow(InvalidRequest::new));
    }
}
