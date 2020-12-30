# Performance Test

`openvidu-recording` docker image 성능 테스트

Usage
```
run.sh [resolution] [number-of-recorders]
```
지원하는 resolution은
- 640x480
- 1280x720
- 1920x1080

Example
```bash
run.sh 1280x720 5
```


# openvidu-recording image
`openvidu-recording`의 repo: https://hub.docker.com/r/openvidu/openvidu-recording/

## Build Manually

```bash
./create_image.sh ubuntu-20-04 83.0.4103.116-1 virnect
```

크롬 버전

https://www.ubuntuupdates.org/package_logs?noppa=&page=1&type=ppas&vals=8#
