<h2 id="section-title">Remote Service Policy</h2>
<hr>

## Why

리모트 서비스 서버는 서비스 제공 및 배포 형태에 따라 Private, Public 형으로 서비스 정책이 구분됩니다.
또한 서비스 제공 및 배포 형태의 다양성 뿐만 아니라 고객사별 제공되는 서비스 다양성이 있기 때문에 서비스 정책에서 대응 가능합니다.

> 서비스 정책 파일이 없거나 제공이되지 않을 경우 기본 서비스 정책이 제공됩니다.

<br>

Limitation
- Company Code는 특정 회사에게만 제공 됩니다.
- GS 구축형에서만 메뉴얼 다운로드 기능이 제공됩니다.
- Production Release시 스토리지, 서버 녹화 서비스가 제공되지 않습니다.

---

## What

현재 서비스 정책 파일은 아래와 같이 제공됩니다:

- **Google text translate**: 사용 여부를 클라이언트에게 제공합니다.
- **Google async speech to text**: 사용 여부를 클라이언트에게 제공합니다.
- **Google sync speech to text**: 사용 여부를 클라이언트에게 제공합니다.
- **Google text to speech**: 사용 여부를 클라이언트에게 제공합니다.
- **Google support language**: 통번역을 제공하는 국가 코드와 이름을 클라이언트에게 제공합니다.
- **Open room service**: 1:n 방식의 화상통화 서비스 제공여부를 클라이언트에게 제공합니다.
- **Storage service**: 스토리지 서비스 기능 제공여부를 클라이언트에게 제공합니다.
- **Server recording service**: 서버 녹화 기능 제공여부를 클라이언트에게 제공합니다.

|          | Private | Public |
| -------- | -------- | ------ |
| Google trans  | Dave     | Steve  |
| Google stt | Gregg    | Karen  |
|     |     |   |

---

## How

You can write Remote Service Policy file (.json)
