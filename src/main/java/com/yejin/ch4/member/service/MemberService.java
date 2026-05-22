package com.yejin.ch4.member.service;

import com.yejin.ch4.common.exception.MemberNotFoundException;
import com.yejin.ch4.image.S3ImageService;
import com.yejin.ch4.member.dto.MemberCreateRequest;
import com.yejin.ch4.member.dto.MemberResponse;
import com.yejin.ch4.member.entity.Member;
import com.yejin.ch4.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3ImageService s3ImageService;

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

    @Transactional
    public void uploadProfileImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        String profileImageKey = s3ImageService.uploadProfileImage(memberId, file);
        member.updateProfileImageKey(profileImageKey);
    }

    @Transactional(readOnly = true)
    public String getProfileImageUrl(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (member.getProfileImageKey() == null) {
            throw new IllegalStateException("등록된 프로필 이미지가 없습니다.");
        }

        return s3ImageService.generatePresignedUrl(member.getProfileImageKey());
    }
}