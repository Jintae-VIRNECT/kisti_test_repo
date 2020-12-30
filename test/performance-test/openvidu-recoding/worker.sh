#!/bin/bash

NAME=$1
RESOLUTION=$2 # one of "640x480", "1280x720", "1920x1080"

IMAGE=openvidu/openvidu-recording
TAG=2.15.0
SHORT_RESOLUTION=$(echo $RESOLUTION | awk -F 'x' '{print $2}')p

URL=http://www.ix264.com/assets/videos/big_buck_bunny_${SHORT_RESOLUTION}.mp4
# URL=https://www.youtube.com/embed/JMuzlEQz3uo?start=1&fs=1&autoplay=1

# RECDIR=/Users/hissinger/dev/virnect/RM-RecordServer/test/recordings
RECDIR=//d/recordings

if [ "$(docker ps -q -f name=openvidu-recording-${NAME})" ]; then
  docker rm -f openvidu-recording-${NAME}
fi

docker run -d --rm \
  -e URL=${URL} \
  -e ONLY_VIDEO=false \
  -e RESOLUTION=${RESOLUTION} \
  -e FRAMERATE=20 \
  -e VIDEO_ID=openvidu-${NAME} \
  -e VIDEO_NAME=openvidu-${NAME} \
  -v ${RECDIR}:/recordings \
  --name openvidu-recording-${NAME} ${IMAGE}:${TAG}

sleep 60

docker exec -d openvidu-recording-${NAME} bash -c "echo 'q' > stop"
