package com.yejin.ch4.member.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "나이는 필수 입력 값입니다.")
    @Min(value = 1, message = "나이는 1 이상이어야 합니다.")
    private Integer age;

    @NotBlank(message = "MBTI는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?i)(E|I)(N|S)(F|T)(P|J)$",
            message = "올바른 MBTI 형식이 아닙니다."
    )
    private String mbti;

}
