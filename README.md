# CH4 AWS Cloud Assignment

## 진행 단계
- LV0 Budget 설정 완료
- LV1 로컬 Member API 구현 완료
- LV1 EC2 배포 및 Health Check 완료
- LV2 RDS & Parameter Store 연동 완료

## LV0 Budget
- 월 예산: $100
- 알림 기준: 80%

![AWS Budget 설정 화면](docs/images/budgets.png)

## LV1 Local API
- POST /api/members
- GET /api/members/{id}
- GET /actuator/health
- local profile: H2
- prod profile: MySQL

## LV1 EC2 배포
- EC2 Public IP: 54.180.133.6
- Health Check URL: http://54.180.133.6:8080/actuator/health
- 상태: UP

![EC2 Health Check 결과](docs/images/health-check.png)

## LV2 RDS & Parameter Store

- Actuator Info URL: http://54.180.133.6:8080/actuator/info
- Parameter Store 확인 값: `{"team-name":"yejin-team"}`
- RDS Inbound Source: EC2 Security Group (`ch4-ec2-sg`)

![RDS Security Group](docs/images/rds-security-group.png)# CH4 AWS Cloud Assignment

## 진행 단계
- LV0 Budget 설정 완료
- LV1 로컬 Member API 구현 완료
- LV1 EC2 배포 및 Health Check 완료

## LV0 Budget
- 월 예산: $100
- 알림 기준: 80%

![AWS Budget 설정 화면](docs/images/budgets.png)

## LV1 Local API
- POST /api/members
- GET /api/members/{id}
- GET /actuator/health
- local profile: H2
- prod profile: MySQL

## LV1 EC2 배포
- EC2 Public IP: 54.180.133.6
- Health Check URL: http://54.180.133.6:8080/actuator/health
- 상태: UP

![EC2 Health Check 결과](docs/images/health-check.png)

## LV2 RDS & Parameter Store

- RDS: MySQL
- Parameter Store: DB 접속 정보 및 team-name 저장
- Actuator Info URL: http://54.180.133.6:8080/actuator/info
- Parameter Store 확인 값: `{"team-name":"yejin-team"}`
- RDS Inbound Source: EC2 Security Group (`ch4-ec2-sg`)

![RDS Security Group](docs/images/rds-security-group.png)

## LV3 S3 Profile Image

- S3 Bucket: ch4-yejin-profile-files
- Public Access Block: Enabled
- Upload API: POST http://54.180.133.6:8080/api/members/{id}/profile-image
- Presigned URL API: GET http://54.180.133.6:8080/api/members/{id}/profile-image
- Presigned URL Expiration: 7 days
- Presigned URL expires at: 2026-05-29

Presigned URL:
https://ch4-yejin-profile-files.s3.ap-northeast-2.amazonaws.com/members/1/profile-6f0cf007-6fc1-465b-9292-1c11f003129d.jpeg?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEE0aDmFwLW5vcnRoZWFzdC0yIkcwRQIgW9T0KGYZzmdaD1mRoMSGwLC%2Bf4ue3pdQpJDPnKJkNC4CIQD2zSKRdMjN8%2FJ0W3bob%2BobAKGNt7FvlQ1hR6S6beOcwCrIBQgWEAAaDDQyMDU1MTI1OTU1NiIMldejKUZJVAbT6AY9KqUFcW9CCr2BBtmmD5jrem1z4QPZiPLZjZF2AIkpC7L6QQv07WurerUSpQFlm6y6cjyvlCwv51WukIUbauNDrZru2jnw30iFKKabhzmvFTG9%2BAxp4s87tv1mF9oDQ74mH4PgzAdXPkSogqz1iPowZnl3kP0CH59JdyvlXXTbi84OAkvu1Wka6M3E1thmzhqNQvNdZBqgbFCGon7ZRe8%2FUIFPs3X8v9%2BgOZksw9PpHa7hB9CE9%2B5sfh314WkdDEHaiWFgB1PuNuHXNMPUfmk%2BNDV3ah2mKrSdZ1g4ZM53Bn2aPCQpui7jZdKdjHmMl5ZQRUIJksMmPkT19kAoykeREivnXmcRFal5JfYQlUzGR5VZ13ti23w9H8sGPKYk6ILxJlWDC8ov8RZWxhcpay2dtCgccxbJnNdEsO9yM3PkifMRJXKVSIKOTlBKRl%2FD7PqJw9dr6p2CiyebWCOWIH3Jl12BJbp03WrIeQrxP%2FK2LTe6T%2FaAImzA0MUO7nGYcNcwCxF1QCSDhoHbd9pe0Pzru%2BG8FJzFXzkvr2exKUtKLbqar0xCXl40kp65uon9ZyCZ7tKeZPCNX1KwD210rCF3D7KZpIO20DGVQmNTU3BdUN4U2LRUXW4ANtXR7DxF0jFehlBX9UnCiXsP7PKwWjjDes7Nb%2Fx0v5bom%2FxAYToTqZagx5rD4MNYR81RsHKESh4v3S2o%2FiS0oYvjvOj68MXSzul81xHyrIUOIME11dndaFUzUUYzjiosBjffLv147YXs7zmC5KdYwHHw2aef8y2VwlRiI7YUBfFvjhfU7VaF46RaxVypckN5uDu7WT3iXjRrcNsAgDKKiRcDq8EeCsGp3Ww38fYFFTrpYfJQewXPXFmqQM6TGRbCZMgZr3DT%2FEr%2FtggS5DwTdGMwsMq%2F0AY6sQFtKm6r8yO8UtZn6w1jYO3%2BSsF5av4FFDadgxDYHaRbN%2F%2BGgSvJcHAHAa7ycrfOb3sWT9j9Gej03PbCYQYKXpk2dMHeuFmiHyEo8GWeWmzKkGoVZovouU8VYaqmU5Kt6js27k6uN8Ubx%2F790O8w7CIOSiXSF4snxwFb8LRo4%2FajynEHzMg4QcZ7ucK4ALHZq4gj2uVMQYBRTX2pawu%2BsmEizU6P0XdNLzwAx0N%2BdHZZzk4%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20260522T061229Z&X-Amz-SignedHeaders=host&X-Amz-Credential=ASIAWD2WO6WSJXEYRR74%2F20260522%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=604800&X-Amz-Signature=49a378145d9ec4affc7179d1cc00783c26d9263b197c1e4e73a72dd3b45327fe