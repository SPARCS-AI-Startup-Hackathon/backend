package com.backend.backend.personalstatement.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalStatementResponse {
    private Long id;
    private String name;
    private String job;
    private String content;

    @Builder
    public PersonalStatementResponse(Long id, String name, String job, String content) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.content = content;
    }
}
