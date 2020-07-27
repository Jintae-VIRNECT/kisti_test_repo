
# Remote Media Server

## Build Remote Media Server Images

```
docker build --rm --tag virnect/remote-mediaserver . -f docker/Dockerfile
```

## Running Remote Media Server

Run:

```bash
docker run --name remote-mediaserver -d -p 8888:8888 virnect/remote-mediaserver
```


미디어 서버 로그 출력

```
docker logs --follow remote-mediaserver >"remote-mediaserver-$(date '+%Y%m%dT%H%M%S').log" 2>&1
```

미디어 서버의 상태를 체크하기 위해서는 다음과 같이

```
$ curl \
    --include \
    --header "Connection: Upgrade" \
    --header "Upgrade: websocket" \
    --header "Host: 127.0.0.1:8888" \
    --header "Origin: 127.0.0.1" \
    http://127.0.0.1:8888/kurento
```

You should get a response similar to this one:

```
HTTP/1.1 500 Internal Server Error
Server: WebSocket++/0.7.0
```

Ignore the "*Server Error*" message: this is expected, and it actually proves that Remote Media Server is up and listening for connections.



## Configuration

미디어 서버 설정 편의를 위해서 다음과 같은 환경 변수를 제공한다.

- GST_DEBUG
- KMS_STUN_IP, KMS_STUN_PORT, KMS_TURN_URL
- KMS_NETWORK_INTERFACES
- KMS_EXTERNAL_ADDRESS
- KMS_MIN_PORT, KMS_MAX_PORT
- KMS_MTU
- KMS_DTLS_PEM_CERT_RSA, KMS_DTLS_PEM_CERT_ECDSA

example

```
docker run --name remote-mediaserver -d -p 8888:8888 \
    -e GST_DEBUG="Kurento*:5" \
    -e KMS_STUN_IP=<stun-url> \
    -e KMS_STUN_PORT=<stun-port> \
    -e KMS_TURN_URL=<user@password:turn-url:port> \
    -e KMS_EXTERNAL_ADDRESS=<public_ip> \
    -e KMS_MIN_PORT=40000 \
    -e KMS_MAX_PORT=50000 \
    -e KMS_MTU=1200 \
    -e KMS_DTLS_PEM_CERT_RSA=/etc/kurento/cert/cert+key.pem \
    -v ${PWD}/cert/cert+key.pem:/etc/kurento/cert/cert+key.pem \
    virnect/remote-mediaserver
```

만약, 직접 설정 파일을 수정하기를 원한다면 `volume`을 사용해서 `/etc/kurento/`의 설정파일을 교체.