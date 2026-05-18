package com.yejin.ch4.common.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super("해당 팀원을 찾을 수 없습니다. id=" + memberId);
    }
}
