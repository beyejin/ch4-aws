package com.yejin.ch4.member.service;

import com.yejin.ch4.common.exception.MemberNotFoundException;
import com.yejin.ch4.member.dto.MemberCreateRequest;
import com.yejin.ch4.member.dto.MemberResponse;
import com.yejin.ch4.member.entity.Member;
import com.yejin.ch4.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        Member newMember = new Member(
                request.getName(),
                request.getAge(),
                request.getMbti().toUpperCase()
        );

        Member saveMember = memberRepository.save(newMember);
        return MemberResponse.from(saveMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member findMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        return MemberResponse.from(findMember);
    }
}