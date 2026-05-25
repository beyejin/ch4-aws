# CH4 AWS Cloud Assignment

Spring Boot 기반 팀원 소개 API를 AWS에 배포하고, RDS, Parameter Store, S3를 활용해 Stateless 아키텍처를 구성한 과제입니다.

## 진행 단계

- LV0 Budget 설정 완료
- LV1 로컬 Member API 구현 완료
- LV1 EC2 배포 및 Health Check 완료
- LV2 RDS & Parameter Store 연동 완료
- LV3 S3 프로필 이미지 업로드 및 Presigned URL 발급 완료
- LV4 Docker & CI/CD 자동화 완료
- LV5 ALB + ASG + HTTPS + Domain 연결 완료

---

## LV0 Budget

- 월 예산: `$100`
- 알림 기준: `80%`

![AWS Budget 설정 화면](docs/images/budgets.png)

---

## LV1 Member API & EC2 배포

### 구현 API

- `POST /api/members`
- `GET /api/members/{id}`

### 운영 설정

- local profile: `H2`
- prod profile: `MySQL`
- Actuator health endpoint 노출
- API 요청 INFO 로그 기록
- 예외 발생 시 ERROR 로그 기록

### EC2 배포 검증

- EC2 Public IP: `54.180.133.6`
- Health Check URL: [http://54.180.133.6:8080/actuator/health](http://54.180.133.6:8080/actuator/health)
- 상태: `UP`

![EC2 Health Check 결과](docs/images/health-check.png)

---

## LV2 RDS & Parameter Store

### 구성 내용

- RDS: `MySQL`
- Parameter Store: DB 접속 정보 및 `team-name` 저장
- Spring Boot prod profile에서 Parameter Store 값 주입
- RDS 보안 그룹 Inbound Source를 EC2 Security Group으로 제한

### 검증 결과

- Actuator Info URL: [http://54.180.133.6:8080/actuator/info](http://54.180.133.6:8080/actuator/info)
- Parameter Store 확인 값: `{"team-name":"yejin-team"}`
- RDS Inbound Source: EC2 Security Group (`ch4-ec2-sg`)

![RDS Security Group](docs/images/rds-security-group.png)

---

## LV3 S3 Profile Image

### 구성 내용

- S3 Bucket: `ch4-yejin-profile-files`
- Public Access Block: `Enabled`
- EC2 IAM Role: `ch4-ec2-s3-role`
- Access Key 없이 IAM Role 기반으로 S3 접근
- DB에는 S3 Object Key 저장
- 이미지 조회 시 Presigned URL 발급

### 구현 API

- Upload API: `POST /api/members/{id}/profile-image`
- Presigned URL API: `GET /api/members/{id}/profile-image`

### Presigned URL 검증

- Presigned URL Expiration: `7 days`
- `X-Amz-Expires=604800` 확인
- Presigned URL expires at: `2026-05-29`

Presigned URL Example:

~~~text
https://ch4-yejin-profile-files.s3.ap-northeast-2.amazonaws.com/members/1/profile-....jpeg?X-Amz-Expires=604800&...
~~~

![Presigned URL Response](docs/images/s3-presigned-url-response.png)
![Presigned URL Access](docs/images/s3-presigned-url-access.png)

---

## LV4 Docker & CI/CD

### 구성 내용

- Docker Image: `hanyejin/ch4-aws:latest`
- Docker Hub: [https://hub.docker.com/r/hanyejin/ch4-aws](https://hub.docker.com/r/hanyejin/ch4-aws)
- GitHub Actions를 통해 Build, Docker Push, EC2 Deploy 자동화
- EC2에서 Docker Container 실행 확인

### 검증 결과

- Health Check URL: [http://54.180.133.6:8080/actuator/health](http://54.180.133.6:8080/actuator/health)
- GitHub Actions 배포 성공
- EC2 `docker ps` 실행 결과 정상

![GitHub Actions 성공 화면](docs/images/github-actions-success.png)
![EC2 Docker ps 결과](docs/images/docker-ps.png)

### CI/CD 트러블슈팅

#### 1. `application-test.yml` profile 설정 오류

GitHub Actions에서 `./gradlew clean build` 실행 중 `InvalidConfigDataPropertyException`이 발생했습니다.

원인은 `application-test.yml` 안에 `spring.profiles.active: test`가 선언되어 있었기 때문입니다. `application-test.yml`은 이미 `test` profile이 활성화되었을 때 읽히는 profile-specific 설정 파일이므로, 그 안에서 다시 active profile을 지정하면 Spring Boot 4에서 잘못된 설정으로 판단합니다.

해결 방법:

- `application-test.yml`에서 `spring.profiles.active` 제거
- 테스트 profile 활성화는 테스트 클래스의 `@ActiveProfiles("test")`로 처리

#### 2. CI 환경의 AWS SDK 빈 생성 실패

첫 번째 오류를 해결한 뒤에는 테스트 컨텍스트 로딩 중 AWS SDK 관련 `SdkClientException`이 발생했습니다.

로컬 환경에는 AWS region 또는 credential 설정이 있었지만, GitHub Actions runner에는 해당 설정이 없어서 `S3Client`, `S3Presigner` 빈 생성이 실패한 것이 원인이었습니다.

해결 방법:

- 테스트용 `application-test.yml`에 H2 DB와 테스트용 AWS dummy 설정 추가
- `Ch4ApplicationTests`에서 AWS SDK 클라이언트를 mock 처리

```java
@MockitoBean
private S3Client s3Client;

@MockitoBean
private S3Presigner s3Presigner;
```

#### 3. EC2 SSH 배포 Secret 형식 오류

Gradle build, Docker image build, Docker Hub push는 성공했지만 EC2 배포 단계에서 SSH 인증이 실패했습니다.

에러 메시지:

```text
ssh.ParsePrivateKey: ssh: no key found
ssh: handshake failed: ssh: unable to authenticate
```

원인은 GitHub Actions secret `EC2_SSH_KEY`에 private key를 등록할 때 마지막 줄에 터미널 출력 문자 `%`가 함께 들어간 것이었습니다.

잘못된 예:

```text
-----END RSA PRIVATE KEY-----%
```

올바른 예:

```text
-----END RSA PRIVATE KEY-----
```

해결 방법:

- `EC2_SSH_KEY` secret에 private key 전체를 줄바꿈 포함해 다시 등록
- 끝에 불필요한 문자나 공백이 들어가지 않았는지 확인
- `EC2_USER`가 AMI에 맞는 사용자명인지 확인

#### 4. ARM 인스턴스와 Docker 이미지 아키텍처 불일치

Private EC2가 `t4g.small (ARM64)` 인스턴스로 생성되었는데, GitHub Actions에서 빌드한 Docker 이미지가 다른 아키텍처 기준으로 올라가면서 Target Group이 계속 `Unhealthy` 상태가 되었습니다.

확인 방법:

- SSM Session Manager로 Private EC2 접속
- `docker ps -a`
- `docker logs ch4-app`

실제 에러:

```text
exec /opt/java/openjdk/bin/java: exec format error
```

해결 방법:

- GitHub Actions Docker build를 `linux/arm64` 기준으로 수정
- ARM64 이미지 재배포 후 Auto Scaling Group이 새 인스턴스를 생성
- Target Group 상태가 `Healthy`로 전환됨

#### 5. GitHub Actions OIDC 권한 설정 오류

Repository Variables로 전환한 뒤, GitHub Actions에서 아래 오류가 발생했습니다.

```text
Could not assume role with OIDC: Not authorized to perform sts:AssumeRoleWithWebIdentity
```

원인은 GitHub Actions가 Assume할 OIDC 전용 IAM Role의 신뢰 정책이 제대로 연결되지 않았기 때문이었습니다.

해결 방법:

- GitHub OIDC용 IAM Role 생성
- `token.actions.githubusercontent.com` provider 연결
- GitHub organization / repository / branch(`main`) 조건 설정
- 해당 Role ARN을 `AWS_ROLE_ARN` 변수에 등록

---

## LV5 ALB + ASG + HTTPS + Domain

### 구성 내용

- NAT Gateway 생성 후 Private Subnet 라우팅을 NAT Gateway로 연결
- Private EC2를 위한 Launch Template 생성
- Auto Scaling Group 생성
- Application Load Balancer(ALB) 생성
- Target Group 생성 및 `/actuator/health` 기준으로 헬스 체크 설정
- Route 53 도메인 연결
- ACM 인증서 적용
- HTTP 요청이 HTTPS로 리다이렉트되도록 리스너 설정

### 배포 결과

- HTTPS Domain URL: [https://api.hanyejin.click](https://api.hanyejin.click)
- Health Check URL: [https://api.hanyejin.click/actuator/health](https://api.hanyejin.click/actuator/health)
- HTTP Redirect 확인: `http://api.hanyejin.click` 접속 시 HTTPS로 자동 전환

![Target Group Healthy](docs/images/target-group-healthy.png)
![HTTPS Domain Health Check](docs/images/https-domain-health.png)

---

## 기술 스택

- Java 17
- Spring Boot 4.0.6
- Spring Data JPA
- H2 Database
- MySQL
- AWS EC2
- AWS RDS
- AWS Systems Manager Parameter Store
- AWS S3
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

## 제출 정보

- GitHub Repository: [https://github.com/beyejin/ch4-aws](https://github.com/beyejin/ch4-aws)
- 구현 단계: `LV5 완료`

### 주요 고민

- Spring Boot 4에서 H2 Console 의존성 분리
- local / test / prod profile 분리
- Parameter Store를 통한 민감 정보 분리
- RDS 보안 그룹 체이닝
- Access Key 없이 IAM Role로 S3 접근
- Presigned URL 7일 만료 설정
- GitHub Actions 기반 Docker build / push / deploy 자동화
- CI 환경에서 외부 AWS 의존성을 제거한 테스트 구성
- ARM64 인스턴스와 Docker 이미지 아키텍처 맞추기
- ALB + ASG + HTTPS + Route 53 기반의 고가용성 구조 구성
