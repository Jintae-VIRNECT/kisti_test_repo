## Author

```
lsb@virnect.com
```

## Description

```
Platform Worstation Web Service
```

## Environment

```
node12 / Yarn workspace
```

## Develop

```
$ yarn
$ yarn workspace workstation dev
```

## Production

```
# 환경설정 확인
$ vim workstation/.env.production

# 빌드 및 실행
$ yarn workspace workstation build
$ yarn workspace workstation deploy
```

## 추가 설명

프로젝트 구조는 https://nuxtjs.org/guides/directory-structure/nuxt  
\+ api, languages, mixins, services

웹 앱 전반에 활성화된 워크스페이스에 따라 달라지는 내용들이 많음.  
활성화 워크스페이스 변경은 필히 vuex의 `activeWorkspace`를 사용하고,  
`services/workspace.watchActiveWorkspace()`를 사용하여 변경 이벤트를 트래킹할 것
