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
