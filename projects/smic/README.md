# smic

## 나중에 고려해야 할 일

- webpack splitChunks - 브라우저 캐시 전략적 활용

## Docker 로컬 테스트해보기

- 빌드

```shell
$ docker build -t smic -f docker/Dockerfile.develop .
```

- 런

```shell
$ docker run smic
```

## 라우팅
`/` 홈  
`/users` 로그인  
`/members` 멤버 목록  
`/contents` 컨텐츠 목록  
└ `/contents/{sceneGroup}` 컨텐츠의 세부공정 목록  
`/process` 공정 목록  
└ `/process/{subProcess}` 공정의 세부공정 목록  
　└ `/process/{subProcess}/{task}` 세부공정의 작업 목록  
`/issue` 이슈 목록  

## 파일 구조

```
smic
|   .babelrc // 바벨 정의
|   .env.local // dotenv local 설정
|   .env.production // dotenv production 설정
|   .eslintrc.js // prettier, eslint 설정
|   package.json
|   README.md
|   route.js // production 환경에서 node.js로 서버 띄울시 적용하는 라우트 설정
|   server.js  // production 환경에서 node.js로 서버 띄울시 적용하는 전체 설정. 상세 설정들은 server 폴더에 있음
|   
|---build // 웹팩 빌드 설정
|   |   webpack.config.base.js // 개발, 배포환경시 공통 설정
|   |   webpack.config.build.js // 배포 환경시(production)
|   |   webpack.config.dev.js // 개발환경시(local)
|---dist // 배포
|---logs // 웹팩 로그파일
|---public // 퍼블릭에 들어갈 index.html, favicon 파일
|---server // production환경에서 서버 띄울때 쓸 서버설정(remote 프로젝트에서 가져옴)
|---ssl // production 환경에서 서버 띄울시 ssl 적용(remote 프로젝트에서 가져옴)
|---src
|   |   assets // style, font, image ....
|   |   components // UI 컴포넌트 정의
|   |   data // 시연용 데이터 정의 - 백엔드 API 교체시 삭제해야함
|   |   mixins // Vue mixins
|   |   models // DB와 밀접한 관계있는 코드 정의
|   |   plugins // 외부 라이브러리 커스텀 or 설정
|   |   router // vue-router 설정
|   |   store // Vuex 설정
|   |   utils // 사용자 정의 파일들
|   |   views // 페이지 기준 뷰 파일들
|   |   App.vue // vue 진입점
|   |   index.js // 루트js 파일
```
