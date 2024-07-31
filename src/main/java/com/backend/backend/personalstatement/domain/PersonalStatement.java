package com.backend.backend.personalstatement.domain;

import com.backend.backend.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PersonalStatement {
    @Id
    @Column(name = "personal_statement_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String job;
    private String name;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    public void update(String content) {
        this.content = content;
    }
    @Builder
    public PersonalStatement(String name, String content, Member member, String job) {
        this.name = name;
        this.content = content;
        this.member = member;
        this.job = job;
    }
}
