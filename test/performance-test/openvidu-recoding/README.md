# Performance Test

`openvidu-recording` docker image 성능 테스트

## Run
성능 테스트를 시작한다. worker.sh를 

Usage
```
run.sh -r RESOLUTION -l LAYOUT_URL -n SESSIONS
```

options:
- r: resolution

    지원하는 resolution은
    - 640x480
    - 1280x720
    - 1920x1080

- l: layout url

    layout 폴더에 녹화 성능측정을 위한 layout page(html)가 있음.
    - 참여자 1명: index1.html
    - 참여자 2명: index2.html
    - 참여자 3명: index3.html
    - 참여자 4명: index4.html
    - 참여자 5명: index5.html
    - 참여자 6명: index6.html

- n: 동시 녹화 session 개수


Example
```bash
run.sh -r 1920x1080 -l http://192.168.13.11:8080/index6.html -n 1
```

## Stop
모든 녹화 중인 docker container를 종료하고 성능 테스트를 종료한다.

Usage
```
stop.sh
```


# openvidu-recording image
`openvidu-recording`의 repo: https://hub.docker.com/r/openvidu/openvidu-recording/

## Build Manually

```bash
./create_image.sh ubuntu-20-04 83.0.4103.116-1 virnect
```

크롬 버전

https://www.ubuntuupdates.org/package_logs?noppa=&page=1&type=ppas&vals=8#
