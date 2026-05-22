package com.yejin.ch4.member.controller;

import com.yejin.ch4.member.dto.MemberCreateRequest;
import com.yejin.ch4.member.dto.MemberResponse;
import com.yejin.ch4.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
        log.info("[API - LOG] POST /api/members");
        MemberResponse responseDto = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 단 건 조회
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        log.info("[API - LOG] GET /api/members/{}", id);
        MemberResponse responseDto = memberService.getMember(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<Void> uploadProfileImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        log.info("[API - LOG] POST /api/members/{}/profile-image", id);
        memberService.uploadProfileImage(id, file);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<String> getProfileImageUrl(@PathVariable Long id) {
        log.info("[API - LOG] GET /api/members/{}/profile-image", id);
        String presignedUrl = memberService.getProfileImageUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }
}