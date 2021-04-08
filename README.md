# PF-WebDownload

## Author
```
lsb@virnect.com / LeeSangBaek
```

## Description
```
버넥트 제품 다운로드 웹 페이지
prod: download.virnect.com
stg: stgdownload.virnect.com
dev: 192.168.6.3:8833
```

## Config
https://github.com/virnect-corp/PF-Configurations/blob/master/develop/download-web-develop.yml

## Environment
```
node ^12.14.1
yarn ^1.21.1

pf-login 프로젝트 필요. 로그인되어 있지 않으면 서버사이드에서 튕겨나감
```

## Architecture
https://ko.nuxtjs.org/
```
Nuxt framework (Universal)

cypress - e2e 테스트
docker
src
/api - 서버 요청 리스트
/languages - 다국어
/middleware - 클라이언트로 보내기 전 서버사이드 로직
/components - 컴포넌트 .vue 파일
/layouts - 페이지 레이아웃 .vue 파일
/pages - 라우팅 되는 페이지 .vue 파일
/plugins - vue root context = Vue.use()
```