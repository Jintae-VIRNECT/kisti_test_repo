# PF-WebWorkstation

## Author
```
lsb@virnect.com / LeeSangBaek
```

## Description
```
버넥트 워크스테이션
prod: workstation.virnect.com
stg: stgworkstation.virnect.com
dev: 192.168.6.3:8878
```

## Config
https://github.com/virnect-corp/PF-Configurations/blob/master/develop/workstation-web-develop.yml

## Environment
```
node ^12.14.1
yarn ^1.21.1

pf-login 프로젝트 필요. 로그인되어 있지 않으면 서버사이드에서 튕겨나감
```

## Architecture
https://ko.nuxtjs.org/
Nuxt framework (Universal)
### folder
```
cypress - e2e 테스트
docker
src
/api - 서버 요청 리스트
/languages - 다국어
/middleware - 클라이언트로 보내기 전 서버사이드 로직
/components - 컴포넌트 .vue 파일
/layouts - 페이지 레이아웃 .vue 파일
/pages - 라우팅 되는 페이지 .vue 파일
/plugins - vue root context (Vue.use())
/models - API 리스폰스 데이터 재정의
/services - UI코드를 제외한 로직 분리
```
### style
scoped 사용하지 않는다. 컨포넌트 이름을 class명으로 정의하고, 페이지는 id로 정의한다.

### logic
워크스페이스 변경시 새로고침 되어야 하는 함수는 `workspaceSerivce.watchActiveWorkspace(fn)` 사용한다.
