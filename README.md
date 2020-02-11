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
Yarn workspace
```

## Build

```
$ yarn
$ yarn worskapce smic build
```

## Running the application

```
# 로컬에서 개발모드로 실행
$ yarn workspace smic local

# production 모드로 실행 -> https://localhost:8887 에 띄움
$ yarn workspace smic start

# 빌드 & production 모드로 실행 
$ yarn workspace smic deploy

```

## 추가 설명

- Yarn workspace 적용
- eslint & prettier 적용

### Yarn workspace 적용

- [Doc 링크](https://yarnpkg.com/en/docs)
- [CLI 사용법](https://yarnpkg.com/en/docs/cli/workspace)

### Eslint & Prettier 설정을 위한 기본 설정

vscode의 settings.json 아래 내용 추가

```json
  ...
  "eslint.alwaysShowStatus": true,
  "eslint.validate": [
    {
        "language": "vue",
        "autoFix": true
    },
    {
        "language": "javascript",
        "autoFix": true
    },
    {
        "language": "javascriptreact",
        "autoFix": true
    }
  ],
  "editor.codeActionsOnSave": {
      "source.fixAll.eslint": true
  }
```

