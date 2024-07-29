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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    private String email;
    private String password;
    private String name;
    private Integer age;
    private String tel;
    private boolean activated;
    @ManyToMany
    private Set<Authority> authorities;

    @Builder
    public Member(String email, String password, String name, Integer age, String tel, boolean activated) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
        this.tel = tel;
        this.activated = activated;
    }
}
