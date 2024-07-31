package com.backend.backend.personalstatement.mapper;

import com.backend.backend.personalstatement.domain.PersonalStatement;
import com.backend.backend.personalstatement.dto.response.PersonalStatementResponse;
import org.springframework.stereotype.Component;


@Component
public class PersonalStatementMapper {

    public PersonalStatementResponse toResponse(PersonalStatement ps) {
        return PersonalStatementResponse.builder()
                .id(ps.getId())
                .name(ps.getName())
                .job(ps.getJob())
                .content(ps.getContent())
                .build();
    }
}
