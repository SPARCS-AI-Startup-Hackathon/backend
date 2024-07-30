package com.backend.backend.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    private String email;
    private String password;
    private String name;
    private Integer age;
    private String tel;
    private String introduction;
    private boolean activated;
    private Integer questionNumber;
    @ManyToMany
    private Set<Authority> authorities;

    @Builder
    public Member(String email, String password, String name, Integer age, String tel, String introduction, boolean activated, Integer questionNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.tel = tel;
        this.introduction = introduction;
        this.activated = activated;
        this.questionNumber = 0;
    }
}

