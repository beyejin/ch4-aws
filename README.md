# CH4 AWS Cloud Assignment

Spring Boot 기반 팀원 소개 API를 AWS에 배포하고, RDS, Parameter Store, S3를 활용해 Stateless 아키텍처를 구성한 과제입니다.

## 진행 단계

- LV0 Budget 설정 완료
- LV1 로컬 Member API 구현 완료
- LV1 EC2 배포 및 Health Check 완료
- LV2 RDS & Parameter Store 연동 완료
- LV3 S3 프로필 이미지 업로드 및 Presigned URL 발급 완료

## LV0 Budget

- 월 예산: $100
- 알림 기준: 80%

![AWS Budget 설정 화면](docs/images/budgets.png)

## LV1 Member API & EC2 배포

### 구현 API

- `POST /api/members`
- `GET /api/members/{id}`

### 운영 설정

- local profile: H2
- prod profile: MySQL
- Actuator health endpoint 노출
- API 요청 INFO 로그 기록
- 예외 발생 시 ERROR 로그 기록

### EC2 배포 검증

- EC2 Public IP: `54.180.133.6`
- Health Check URL: http://54.180.133.6:8080/actuator/health
- 상태: `UP`

![EC2 Health Check 결과](docs/images/health-check.png)

## LV2 RDS & Parameter Store

### 구성 내용

- RDS: MySQL
- Parameter Store: DB 접속 정보 및 `team-name` 저장
- Spring Boot prod profile에서 Parameter Store 값 주입
- RDS 보안 그룹 Inbound Source를 EC2 Security Group으로 제한

### 검증 결과

- Actuator Info URL: http://54.180.133.6:8080/actuator/info
- Parameter Store 확인 값: `{"team-name":"yejin-team"}`
- RDS Inbound Source: EC2 Security Group (`ch4-ec2-sg`)

![RDS Security Group](docs/images/rds-security-group.png)

## LV3 S3 Profile Image

### 구성 내용

- S3 Bucket: `ch4-yejin-profile-files`
- Public Access Block: Enabled
- EC2 IAM Role: `ch4-ec2-s3-role`
- Access Key 없이 IAM Role 기반으로 S3 접근
- DB에는 S3 Object Key 저장
- 이미지 조회 시 Presigned URL 발급

### 구현 API

- Upload API: `POST http://54.180.133.6:8080/api/members/{id}/profile-image`
- Presigned URL API: `GET http://54.180.133.6:8080/api/members/{id}/profile-image`

### Presigned URL 검증

- Presigned URL Expiration: 7 days
- `X-Amz-Expires=604800` 확인
- Presigned URL expires at: `2026-05-29`

Presigned URL:

```text
https://ch4-yejin-profile-files.s3.ap-northeast-2.amazonaws.com/members/1/profile-6f0cf007-6fc1-465b-9292-1c11f003129d.jpeg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEE0aDmFwLW5vcnRoZWFzdC0yIkcwRQIgW9T0KGYZzmdaD1mRoMSGwLC%2Bf4ue3pdQpJDPnKJkNC4CIQD2zSKRdMjN8%2FJ0W3bob%2BobAKGNt7FvlQ1hR6S6beOcwCrIBQgWEAAaDDQyMDU1MTI1OTU1NiIMldejKUZJVAbT6AY9KqUFcW9CCr2BBtmmD5jrem1z4QPZiPLZjZF2AIkpC7L6QQv07WurerUSpQFlm6y6cjyvlCwv51WukIUbauNDrZru2jnw30iFKKabhzmvFTG9%2BAxp4s87tv1mF9oDQ74mH4PgzAdXPkSogqz1iPowZnl3kP0CH59JdyvlXXTbi84OAkvu1Wka6M3E1thmzhqNQvNdZBqgbFCGon7ZRe8%2FUIFPs3X8v9%2BgOZksw9PpHa7hB9CE9%2B5sfh314WkdDEHaiWFgB1PuNuHXNMPUfmk%2BNDV3ah2mKrSdZ1g4ZM53Bn2aPCQpui7jZdKdjHmMl5ZQRUIJksMmPkT19kAoykeREivnXmcRFal5JfYQlUzGR5VZ13ti23w9H8sGPKYk6ILxJlWDC8ov8RZWxhcpay2dtCgccxbJnNdEsO9yM3PkifMRJXKVSIKOTlBKRl%2FD7PqJw9dr6p2CiyebWCOWIH3Jl12BJbp03WrIeQrxP%2FK2LTe6T%2FaAImzA0MUO7nGYcNcwCxF1QCSDhoHbd9pe0Pzru%2BG8FJzFXzkvr2exKUtKLbqar0xCXl40kp65uon9ZyCZ7tKeZPCNX1KwD210rCF3D7KZpIO20DGVQmNTU3BdUN4U2LRUXW4ANtXR7DxF0jFehlBX9UnCiXsP7PKwWjjDes7Nb%2Fx0v5bom%2FxAYToTqZagx5rD4MNYR81RsHKESh4v3S2o%2FiS0oYvjvOj68MXSzul81xHyrIUOIME11dndaFUzUUYzjiosBjffLv147YXs7zmC5KdYwHHw2aef8y2VwlRiI7YUBfFvjhfU7VaF46RaxVypckN5uDu7WT3iXjRrcNsAgDKKiRcDq8EeCsGp3Ww38fYFFTrpYfJQewXPXFmqQM6TGRbCZMgZr3DT%2FEr%2FtggS5DwTdGMwsMq%2F0AY6sQFtKm6r8yO8UtZn6w1jYO3%2BSsF5av4FFDadgxDYHaRbN%2F%2BGgSvJcHAHAa7ycrfOb3sWT9j9Gej03PbCYQYKXpk2dMHeuFmiHyEo8GWeWmzKkGoVZovouU8VYaqmU5Kt6js27k6uN8Ubx%2F790O8w7CIOSiXSF4snxwFb8LRo4%2FajynEHzMg4QcZ7ucK4ALHZq4gj2uVMQYBRTX2pawu%2BsmEizU6P0XdNLzwAx0N%2BdHZZzk4%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260522T061229Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAWD2WO6WSJXEYRR74%2F20260522%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=49a378145d9ec4affc7179d1cc00783c26d9263b197c1e4e73a72dd3b45327fe
```

기술 스택
Java 17
Spring Boot 4.0.6
Spring Data JPA
H2 Database
MySQL
AWS EC2
AWS RDS
AWS Systems Manager Parameter Store
AWS S3
AWS IAM Role
Gradle
제출 정보
GitHub Repository: https://github.com/beyejin/ch4-aws
구현 단계: LV3 완료
주요 고민:
Spring Boot 4에서 H2 Console 의존성 분리
local/prod profile 분리
Parameter Store를 통한 민감 정보 분리
RDS 보안 그룹 체이닝
Access Key 없이 IAM Role로 S3 접근
Presigned URL 7일 만료 설정

## LV4 Docker & CI/CD

- Docker Image: `hanyejin/ch4-aws:latest`
- Docker Hub: https://hub.docker.com/r/hanyejin/ch4-aws
- GitHub Actions: Build, Docker Push, EC2 Deploy 자동화
- EC2 Docker Container: Running
- Health Check URL: http://54.180.133.6:8080/actuator/health

![GitHub Actions 성공 화면](docs/images/github-actions-success.png)

![EC2 Docker ps 결과](docs/images/docker-ps.png)