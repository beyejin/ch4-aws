# CH4 AWS Cloud Assignment

Spring Boot 기반 팀원 소개 API를 AWS에 배포하고,  
RDS, Parameter Store, S3, CloudFront를 활용해 Stateless 아키텍처를 구성한 과제입니다.

---

# 프로젝트 진행 단계

- LV0 Budget 설정 완료
- LV1 Member API 구현 및 EC2 배포 완료
- LV2 RDS & Parameter Store 연동 완료
- LV3 S3 프로필 이미지 업로드 완료
- LV4 Docker & GitHub Actions 기반 CI/CD 자동화 완료
- LV5 ALB + ASG + HTTPS + Domain 연결 완료
- LV6 CloudFront CDN 적용 완료

---

# LV0 Budget

- 월 예산: `$100`
- 알림 기준: `80%`

![AWS Budget 설정 화면](docs/images/budgets.png)

---

# LV1 Member API & EC2 배포

## 구현 API

- `POST /api/members`
- `GET /api/members/{id}`

## 운영 설정

- local profile: `H2`
- prod profile: `MySQL`
- Actuator health endpoint 노출
- API 요청 INFO 로그 기록
- 예외 발생 시 ERROR 로그 기록

## 배포 검증

- EC2 Public IP: `54.180.133.6`
- Health Check URL:
  `http://54.180.133.6:8080/actuator/health`
- 상태: `UP`

![EC2 Health Check 결과](docs/images/health-check.png)

---

# LV2 RDS & Parameter Store

## 구성 내용

- RDS(MySQL) 구성
- Parameter Store에 DB 정보 및 `team-name` 저장
- Spring Boot prod profile에서 Parameter Store 값 주입
- RDS Security Group Source를 EC2 Security Group으로 제한

## 검증 결과

- Actuator Info URL:
  `http://54.180.133.6:8080/actuator/info`

- Parameter Store 값:
  `{"team-name":"yejin-team"}`

![RDS Security Group](docs/images/rds-security-group.png)

---

# LV3 S3 Profile Image

## 구성 내용

- S3 Bucket:
  `ch4-yejin-profile-files`

- Public Access Block 활성화

- IAM Role 기반 S3 접근

- DB에는 S3 Object Key 저장

- 이미지 조회 시 Presigned URL 발급

## 구현 API

- Upload API:
  `POST /api/members/{id}/profile-image`

- Image API:
  `GET /api/members/{id}/profile-image`

## 검증 결과

- Presigned URL Expiration:
  `7 days`

- `X-Amz-Expires=604800` 확인

### Presigned URL Example

```text
[https://ch4-yejin-profile-files.s3.ap-northeast-2.amazonaws.com/...](https://ch4-yejin-profile-files.s3.ap-northeast-2.amazonaws.com/https%3A//d1rt3b9hert9ls.cloudfront.net/members/1/profile-9daaec7a-0da0-4f79-b52a-b74603208fbe.jpeg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEKj%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIge%2BxohYlE9CLWmENCzhPiPJUTar%2FKwem3r67fVshV4BcCIEH5NH0fBofj8Va%2BWrZmDmJfionC0KEVVxGRmmtcqOeUKsgFCHEQABoMNDIwNTUxMjU5NTU2IgyepB1llzQCVZHmsKsqpQWO3sAsg65KD0DfBebn9TZDkUF4hv%2BfPnbpbeJnQdZTUT9BC41l4zvN4t6A6%2FTtCjiPyxPqA9ghQIiM%2FsQqrdd60qfKYNIxx7NrgbXdo6YJfK383wxNjjXT8rStUaEzRzG59X2Sk4%2B8wHn4FAsLbc3TP5S%2Bah59NmrHb20vutUA0%2FJZhyQNQmnOI1gc8id5F0oY61ZvG27nl6ZyIeCbdc%2F9z5HYLGK%2BynfRaXYqhO237VJ4Z1Ne7v3H3TRFGB0%2FamOaDdla56tFFNeH5sz6aSLE6KN4gxOwBb10wz%2BLGpYMGLcV5IDYC93JNNEAAKaPk61V2x2A8aSyb1SGbMr5uA2oThu8uo98Ewd4q5OH2BDwSRk1iRCpV4O9vNh9a4BzoD9ztez03TE4ehs4DS5dFa5g7QbLTzViHywkORhs2%2BoUu49pfCi0ig281hLbxVnQY3dD8zHsBWY%2FsW1tHCkHSA9Tc2VrzmIoT6DVevJcqLTQgQncSOHXTkrGtap4KUb5GhD0zfA8cmX1G2qyWolslJ4FxhfB9GxwPhPteBFbgKYry0dLDWSJEhm0wSGMZ%2B1Tf5sFKv%2BxrLVhwgNN0Gvg8ZukvZ6VthlOsbuze7AckvrMy4uGjAxZ9OaDks5GSVYhkYBakZNXV5%2FhGd4SazRgxJVngpwVxQtZtGBhnCJfkvgg%2FUsvI%2B2lC33elrYjXhguGPfTwxZ7CDHzodTGztKGnVMBJwPz9byk8YyLkhXg8ZmzVpMmdbPIzclyoZ%2FO4ibAWF7W1HUTxIoSmmBkOWmDloqtt9B0xg9AFZRkvQkZW1La3Qc5r5K%2BpPgQF%2FkSkztwDsuOzty%2BWobAoDEYgvnrpUTr3tTcZeSGv6kvczDaJd0gRTkrP5d9jucVoX%2BbJdi%2BDjebJrdzSjCBw9PQBjqyATECiXs7E3AAsvFzaGcGLYCsEjw44KFEsIA%2BU3wWLv5y9YC0pm6WR2u%2FKBBp7l9RmGWjK6l1rl6EvkTminD%2BzffI9Js1lD3LW8OYdZiCPAFFzkaiU7Uwl9x1xy3r6asVRui6gvpi5IjsJo%2FxyTQH2cUa6aBTd%2BApGZ8yU9CNk9FHvxn3zEx9ldZJaf6C4hayQeF7p5i1iHNivNx2QWRm3KrKyF1YEYC4RDf3xWe%2BukMyB%2Fs%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260526T025842Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAWD2WO6WSKCRJEBBX%2F20260526%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=87e4b98848a0586d73dac4d44671c6d009aba46bd418aba49e621017b911915b)
```

![Presigned URL Access](docs/images/s3-presigned-url-access.png)

---

# LV4 Docker & CI/CD

## 구성 내용

- Docker Image:
  `hanyejin/ch4-aws:latest`

- GitHub Actions 기반 자동 배포 구성

- Docker Hub Push 자동화

- EC2 Docker Deploy 자동화

## 검증 결과

- GitHub Actions 배포 성공

- Docker Container 실행 확인

- Health Check 정상 동작 확인

![GitHub Actions 성공 화면](docs/images/github-actions-success.png)

![EC2 Docker ps 결과](docs/images/docker-ps.png)

---

# LV4 CI/CD 트러블슈팅

## 1. application-test.yml profile 설정 오류

GitHub Actions build 중  
`InvalidConfigDataPropertyException` 발생.

### 원인

`application-test.yml` 내부에  
`spring.profiles.active: test`  
설정이 존재했기 때문.

### 해결

- `spring.profiles.active` 제거
- `@ActiveProfiles("test")` 사용

---

## 2. AWS SDK 빈 생성 실패

테스트 환경에서 AWS region/credential이 없어  
`SdkClientException` 발생.

### 해결

- 테스트용 dummy AWS 설정 추가
- AWS SDK Bean Mock 처리

```java
@MockitoBean
private S3Client s3Client;

@MockitoBean
private S3Presigner s3Presigner;
```

---

## 3. EC2 SSH Secret 오류

GitHub Actions SSH 인증 실패.

### 원인

Private Key 마지막에 `%` 문자 포함.

### 해결

- Secret 재등록
- 불필요한 문자 제거

---

## 4. ARM64 Docker 이미지 오류

Private EC2(`t4g.small`)와  
Docker Image 아키텍처 불일치 발생.

### 실제 에러

```text
exec /opt/java/openjdk/bin/java: exec format error
```

### 해결

- Docker build를 `linux/arm64` 기준으로 수정
- ARM64 이미지 재배포

---

## 5. GitHub Actions OIDC 권한 오류

### 에러

```text
Could not assume role with OIDC
```

### 해결

- GitHub OIDC IAM Role 생성
- `token.actions.githubusercontent.com` 연결
- Repository/Branch 조건 설정

---

# LV5 ALB + ASG + HTTPS + Domain

## 구성 내용

- NAT Gateway 생성
- Private EC2 Launch Template 구성
- Auto Scaling Group 구성
- ALB + Target Group 생성
- `/actuator/health` 기준 헬스 체크 설정
- Route53 Domain 연결
- ACM HTTPS 인증서 적용
- HTTP → HTTPS Redirect 설정

## 검증 결과

- HTTPS Domain:
  `https://api.hanyejin.click`

- HTTPS Health Check 정상 동작

- HTTP 접근 시 HTTPS Redirect 확인

![Target Group Healthy](docs/images/target-group-healthy.png)

![HTTPS Domain Health Check](docs/images/https-domain-health.png)

---

# LV6 CloudFront CDN

## 구성 내용

- CloudFront Distribution 생성
- S3 앞단에 CDN 구성
- CloudFront Domain 기반 이미지 제공
- Spring Boot 설정에 CloudFront Domain 추가

```yml
cloud:
  aws:
    s3:
      bucket: ch4-yejin-profile-files
    cloudfront:
      domain: d1rt3b9hert9ls.cloudfront.net
```

## 구현 내용

S3 Object Key를 기반으로  
CloudFront URL 반환 기능 구현.

```java
private String createCloudFrontUrl(String key) {
    return "https://" + cloudFrontDomain + "/" + key;
}
```

## 구현 API

- Upload API:
  `POST /api/members/{id}/profile-image`

## 검증 결과

- CloudFront URL 생성 확인
- CloudFront URL 기반 이미지 조회 성공

### CloudFront URL Example

```text
https://d1rt3b9hert9ls.cloudfront.net/members/1/profile-image.jpeg
```

![CloudFront Image Access](docs/images/cloudfront-image-access.png)

![CloudFront URL Response](docs/images/cloudfront-url-response.png)

---

# 기술 스택

- Java 17
- Spring Boot 4.0.6
- Spring Data JPA
- H2 Database
- MySQL
- AWS EC2
- AWS RDS
- AWS S3
- AWS CloudFront
- AWS IAM Role
- AWS ALB
- AWS Auto Scaling
- AWS Route 53
- AWS ACM
- Docker
- Docker Hub
- GitHub Actions
- Gradle

---

# 주요 고민 및 학습 내용

- local / test / prod profile 분리
- Parameter Store 기반 민감 정보 관리
- IAM Role 기반 S3 접근
- Presigned URL 만료 설정
- GitHub Actions 기반 자동 배포
- ARM64 환경 대응
- ALB + ASG 기반 고가용성 구성
- CloudFront 기반 이미지 전송 최적화

---

# 제출 정보

- GitHub Repository:
  https://github.com/beyejin/ch4-aws

- 구현 단계:
  `LV6 완료`
