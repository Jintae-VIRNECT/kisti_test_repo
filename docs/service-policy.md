<h2 id="section-title">Service Policy</h2>
<hr>

- **[Google translate api](https://cloud.google.com/translate/docs?hl=ko)**
- **[Intellij markdown](https://www.jetbrains.com/help/idea/markdown.html)**

---

## Why

서비스 제공 및 배포 형태에 따라 Private, Public 형으로 서비스 정책이 구분됩니다.
또한 서비스 제공 및 배포 형태의 다양성 뿐만 아니라 고객사별 제공되는 서비스 다양성이 있기 때문에 서비스 정책 파일 또는 DB의 고객사 정보를 통해 대응 가능합니다.

> 컴패니 코드와 함께 고객사 정보가 존재할 경우 DB의 저장된 서비스 정책이 제공됩니다.
> 고객사 정보 혹은 컴패니 코드가 없을 경우 서비스 정책 파일(policy/service-policy.json)을 통해 서비스 정책이 결정됩니다.
> 서비스 정책 파일이 없거나 제공이되지 않을 경우 소스상으로 구현된 기본 서비스 정책이 제공됩니다. 

***여러 고객사별 대응하기 위해서는 DB의 Company table을 참고하십시오.***

<br>

## What

현재 서비스 정책 파일은 아래와 같이 제공됩니다:
- **Google text translate**: 사용 여부를 클라이언트에게 제공합니다.
- **Google async speech to text**: 사용 여부를 클라이언트에게 제공합니다.
- **Google sync speech to text**: 사용 여부를 클라이언트에게 제공합니다.
- **Google text to speech**: 사용 여부를 클라이언트에게 제공합니다.
- **Google support language**: 통번역을 제공하는 국가 코드와 이름을 클라이언트에게 제공합니다.
- **Session Private**: 초대된 사용자만 참여가능한 화상통화 서비스를 클라이언트에게 제공합니다.
- **Session Public**: 접속한 모든 사용자가 참여가능한 화상통화 서비스 클라이언트에게 제공합니다.
- **Storage service**: 스토리지 서비스 기능 제공여부를 클라이언트에게 제공합니다.
- **Server recording service**: 서버 녹화 기능 제공여부를 클라이언트에게 제공합니다.

|                          | Private  | Public |
| --------------------     | -------- | ------ |
| Google translate         | O        | O      |
| Google stt sync          | O        | O      |
| Google stt streaming     | O        | O      |
| Google tts               | O        | O      |
| storage service          | O        | X      |
| recording service        | O        | X      |
| Session Private          | O        | O      |
| Session Public           | O        | O      |

---

## How

### Company code

컴패니 코드는 고객사 별로 부가 서비스를 별도 제공하기 위해 사용됩니다.

[1]: /service-api/src/main/java/com/virnect/service/constraint/CompanyConstants.java
```java
/**
 * Hexademical Company code
 */
public class CompanyConstants {
    public static final int COMPANY_DEFAULT = 0x0000;
}
```

### Session type

세션 타입은 아래와 같이 제공됩니다.

[2]: /service-data/src/main/java/com/virnect/data/dao/Company.java
```java
public enum SessionType {
    PRIVATE, // non-disclosure conference session
    PUBLIC,  // public conference session
    OPEN     // public streaming session
}
```

### License name

라이선스 이름은 아래와 같이 제공 됩니다.

[3]: /service-api/src/main/java/com/virnect/service/constraint/LicenseConstants.java
```java
public class LicenseConstants {
    public static final String LICENSE_PRODUCT = "product";
    public static final String LICENSE_UNOFFICIAL = "unofficial";
}
```
### Write service policy file (.json)

별도의 정책 파일을 작성할 경우 아래와 같이 제공 됩니다.

> 특정 경로에서 정책 파일을 불러올 경우 Dockerfile에서 host file system과 container file system이 연결되었는지 확인합니다.
> configuration properties의 service.policy.location에 해당 정책 파일 경로를 작성하십시오.

[4]: /service-server/src/main/resources/policy/privateServicePolicy.json
```json
{
  "company_info": {
    "company_code": 0,
    "workspace_id": "",
    "license_name": "product",
    "session_type": "open",
    "recording": true,
    "storage": true,
    "translation": true,
    "stt_sync": true,
    "stt_streaming": true,
    "tts": true,
    "language_codes": [
      {
        "code": "ko-KR",
        "text": "한국어"
      },
      {
        "code": "en-US",
        "text": "English"
      }
    ]
  }
}
```
