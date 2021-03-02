# Remote Service Roadmap

## Introduction

This section elaborates on proposed new features or tasks which are expected to
be added to the product in the foreseeable future. There should be no assumption
of a commitment to deliver these features on specific dates or in the order
given. The development team will be doing their best to follow the proposed
dates and priorities, but please bear in mind that plans to work on a given
feature or task may be revised. All information is provided as a general
guidelines only, and this section may be revised to provide newer information at
any time.

## New Features

- Company Code 별 제공 기능이 세분화하여 제공됩니다.
- 원격협업과 오픈방 구분 제공됩니다.
- 원격협업과 오픈방 히스토리가 구분 제공됩니다.
- 통번역 서비스 제공 시, STT, TTS, Translate 선택 제공이 가능합니다.
- 2개 이상의 미디어 서버 등록 기능이 추가되었습니다.
- 2개 이상의 턴 서버 등록 기능이 추가되었습니다.
- API 응답 처리 값이 수정되었습니다.
- 녹화 서버의 녹화 종료 API가 연동 되었습니다.
- 설치 환경별 설정이 수정되었습니다.

## Improvement
- 협업방 프로필 업로드 시, 반환 값에 확장자가 포함되도록 수정되었습니다.

### Bug Fixes
- 오픈방 사용 시, 동일 계정 접속이 수정되었습니다.
- 오픈방 사용 시, 원격협업 리스트에 동일한 세션정보가 노출되는 이슈가 수정되었습니다.
- 오픈방 사용 시, 참여자가 중복 적재되는 이슈가 수정되었습니다.
- 오픈방 사용 시, 참여자 숫자가 제한 인원을 초과하는 이슈가 수정되었습니다.
- 히스토리 리스트(협업방, 오픈방)가 정상적으로 노출되지 않던 이슈가 수정되었습니다.
- 오픈방 생성 시 타 유저에게 오픈방 정보가 노출되지 않던 이슈가 수정되었습니다.
- 오픈방 리더가 방을 나갔을 때 리더 정보가 undefined되던 이슈가 수정되었습니다.
- v2.2 기획서 기준으로 파일 업로드 확장자가 추가되었습니다.
- 파일 업로드시 지원하지 않은 확장자 등의 예외에 따른 응답 시, 에러코드와 함께 반환 하도록 수정되었습니다.
- 프로필 업로드 시 최대 용량 5MB(5242880) 이상으로 업로드 하지 못하도록 수정되었습니다.

### Limitation
- Company Code는 특정 회사에게만 제공 됩니다.
- 오픈방은 특정 회사에게만 제공됩니다.
- 통번역은 특정 회사에게만 제공됩니다.
- 스토리지 서비스는 특정 회사에게만 제공됩니다.
- 서버 녹화 서비스는 특정 회사에게만 제공됩니다.

### Refactoring
- Build script 수정
- 사용되지 않는 Library 제거



### AWS S3 Bucket
* Added and linked bucket for remote room profile image upload

### File Rest API Update
* GET /remote/file **Load Remote Session File List**
* DELETE /remote/file/{workspaceId}/{sessionId} **Delete the specific fileDelete a Specific file**
* POST /remote/file/{workspaceId}/{sessionId}/profile **Update a Remote Session profile image file**
* GET /remote/file/download/{workspaceId}/{sessionId} **Download a Specific file**
* GET /remote/file/download/url/{workspaceId}/{sessionId} **Get URL to downloadGet URL to download**
* POST /remote/file/upload **Upload a file to Storage Server**

###  Bug Fixes
* Improvement in Invite a member to a specific remote session
* Improvement in force disconnect member from a specific remote session
* Send Push message using message server
* Improvement in Invite a member when re-invite member after member is kicked out 

# Next release
* API DELETE /remote/room/{workspaceId}/{sessionId}/member **(Deprecated)**
* API GET /remote/file/download/{workspaceId}/{sessionId} **(Deprecated)**
* API for user account exit scheduled to be added.
* Add and link bucket for remote room file share.


# Medium term
* On premises installation license management
* Kurento media server discovery 

# Long term
* ... 
