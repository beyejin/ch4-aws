package com.yejin.ch4.member.repository;

import com.yejin.ch4.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
