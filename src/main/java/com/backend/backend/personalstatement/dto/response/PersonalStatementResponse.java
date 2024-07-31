package com.backend.backend.personalstatement.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PersonalStatementResponse {
    private String name;
    private String content;

    @Builder
    public PersonalStatementResponse(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
