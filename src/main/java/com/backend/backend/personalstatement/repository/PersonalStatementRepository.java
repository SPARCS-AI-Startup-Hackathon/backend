package com.backend.backend.personalstatement.repository;

import com.backend.backend.member.domain.Member;
import com.backend.backend.personalstatement.domain.PersonalStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalStatementRepository extends JpaRepository<PersonalStatement, Long> {

    List<PersonalStatement> findAllByMember(Member member);
}
