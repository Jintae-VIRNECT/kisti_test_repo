#!/bin/bash

NAME=$1
RESOLUTION=$2 # one of "640x480", "1280x720", "1920x1080"
LAYOUT_URL=$3

IMAGE=rm-recordserver-agent
TAG=latest

# RECDIR=/Users/hissinger/dev/virnect/RM-RecordServer/test/recordings
RECDIR=/home/esahn/dev/virnect/RM-RecordServer/test/performance-test/openvidu-recoding/recordings

if [ "$(docker ps -q -f name=openvidu-recording-${NAME})" ]; then
  docker rm -f openvidu-recording-${NAME}
fi

docker run -d --rm \
  -e URL=${LAYOUT_URL} \
  -e ONLY_VIDEO=false \
  -e RESOLUTION=${RESOLUTION} \
  -e FRAMERATE=20 \
  -e VIDEO_ID=openvidu-${NAME} \
  -e VIDEO_NAME=openvidu-${NAME} \
  -v ${RECDIR}:/recordings \
  --name openvidu-recording-${NAME} ${IMAGE}:${TAG}

sleep 60

docker exec -d openvidu-recording-${NAME} bash -c "echo 'q' > stop"
