package com.yejin.ch4.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, length = 4)
    private String mbti;

    private String profileImageKey;

    public Member(String name, int age, String mbti) {
        this.name = name;
        this.age = age;
        this.mbti = mbti;
    }

    public void updateProfileImageKey(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }
}

