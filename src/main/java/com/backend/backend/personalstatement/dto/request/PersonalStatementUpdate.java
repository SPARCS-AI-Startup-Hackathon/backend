package com.backend.backend.personalstatement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PersonalStatementUpdate {

    @NotBlank
    private String content;

    @Builder
    public PersonalStatementUpdate(String content) {
        this.content = content;
    }
}
