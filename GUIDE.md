## VIRNECT Spring Boot Project Guideline

### 요구사항 
- Java 1.8 ( OpenJDK )
- Intellij or sts or other ide ( intellij community version is free )
- git
- database engine (Mysql or H2)

### #1 프로젝트 아키텍처 (하나의 도메인을 다루는 경우)
```
.
├── src
    ├── main
        ├── docker // 운영 또는 브랜치 전략에 맞는 Dockerfile들로 구성됨
            ├── Dockerfile.local
            ├── Dockerfile.staging
            ├── Dockerfile.develop
            └── Dockerfile.production
        └── generated // QueryDSL Q Class들이 위치함
        ├── java
            ├── com.virnect.${projectname} // ex) com.virnect.gatewayserver
                ├── api // 컨트롤러 클래스로 구성
                ├── application // 서비스 클래스들로 구성됨 (ex: licenseRegisterService, ProductRestService(외부 서버 통신))
                ├── dao // dao(data access object) or repository 관련 클래스들로 구성
                ├── domain // 도메인 엔티티 클래스들로 구성됨 (해당 도메인에 종속된 Embeddable, enum 클래스 포함)
                        └── BaseEntity.java
                ├── dto // 주로 request, response 객체로 구성(ex: licenseRegisterDto)
                ├── Exception // 해당 도메인이 발생시키는 Exception 클래스들로 구성 (EX: 아래는 예시)
                        ├── CustomRuntimeException1.java
                        └── CustomRuntimeException2.java
                ├── global // 프로젝트 전체에 적용되는 클래스로 구성됨
                        ├── common // 공통으로 사용되는 value 클래스로 구성
                        ├── config // 스프링 및 프로젝트 관련 설정 클래스들로 구성
                        ├── error // 예외 핸들링을 담당하는 클래스들로 구성
                        └── util // 유틸성 클래스들로 구성
                ├── infra // 외부 인프라 관련 클래스로 구성됨 (아래는 예시)
                        ├── email 
                            ├── AmazonSimpleEmailClient.java
                            └── JavaSimpleEmailClient.java
                        ├── sms
                            ├── AmazonSmsClient.java
                            └──  SmsClient.java
                        └──  file
                            ├── AwsS3fileUploader.java
                            └── LocalFileUploader.java
        └── resources // 각종 리소스 파일들이 위치함
            ├── templates // 각종 템플릿 파일들이 위치함
                └── login.html
            ├── application-local.yml // 프로젝트 설정파일은 운영 또는 브랜치 전략에 맞게 분리하여 구성
            ├── application-develop.yml
            ├── application-production.yml
            └── application-staging.yml

```

### #2 프로젝트 아키텍처 (여러개의 도메인을 다루는 경우)
```
.
├── src
    ├── main
        ├── docker // 운영 또는 브랜치 전략에 맞는 Dockerfile들로 구성됨
            ├── Dockerfile.local
            ├── Dockerfile.staging
            ├── Dockerfile.develop
            └── Dockerfile.production
        └── generated // QueryDSL Q Class들이 위치함
        ├── java
            ├── com.virnect.${projectname} // ex) com.virnect.gatewayserver
                ├── domain // 도메인을 담당
                    ├── license // 예시: 라이선스 도메인이 관련되는 경우
                        ├── api // 컨트롤러 클래스로 구성, 외부 rest api 클래스도 위치
                        ├── application // 해당 서비스 클래스들로 구성됨 (ex: licenseRegisterService, ProductRestService)
                        ├── dao // dao(data access object) or repository 관련 클래스들로 구성
                        ├── domain // 해당 도메인의 엔티티 클래스들로 구성됨 (해당 도메인에 종속된 Embeddable, enum 클래스 포함)
                        ├── dto // 주로 request, response 객체로 구성(ex: licenseRegisterDTO)
                        ├── Exception // 해당 도메인이 발생시키는 Exception 클래스들로 구성
                ├── model // 도메인 Entity 객체들이 공통으로 사용할 클래스로 구성
                ├── global // 프로젝트 전체에 적용되는 클래스로 구성됨
                        ├── common // 공통으로 사용되는 value 클래스로 구성
                        ├── config // 스프링 및 프로젝트 관련 설정 클래스들로 구성
                        ├── error // 예외 핸들링을 담당하는 클래스들로 구성
                        └── util // 유틸성 클래스들로 구성
                ├── infra // 외부 인프라 관련 클래스로 구성됨 (아래는 예시)
                        ├── email 
                            ├── AmazonSimpleEmailClient.java
                            └── JavaSimpleEmailClient.java
                        ├── sms
                            ├── AmazonSmsClient.java
                            └──  SmsClient.java
                        └──  file
                            ├── AwsS3fileUploader.java
                            └── LocalFileUploader.java
        └── resources // 각종 리소스 파일들이 위치함
            ├── templates // 각종 템플릿 파일들이 위치함
                └── login.html
            ├── application-local.yml // 프로젝트 설정파일은 운영 또는 브랜치 전략에 맞게 분리하여 구성
            ├── application-develop.yml
            ├── application-production.yml
            └── application-staging.yml

```

### 프로젝트 빌드관련 Gradle 기본 명령어

- Gradle 관련 명령어
```groovy
gradle clean // 빌드 패키지들을 삭제하고 모두 초기화
gradle build // 현재 프로젝트를 빌드함, (프로젝트 내의 Test 코드들을 먼저 실행한 뒤에 통과되면 빌드)
gradle build -x test // 현재 프로젝트를 빌드함, (프로젝트 내의 Test 코드들을 실행하지 않고 바로 빌드)
```

- 프로젝트 루트 경로로 터미널을 열고, 아래 명령어를 실행한다(Gradle Wrapper를 사용)
```groovy
# ./gradlew clean build
```
