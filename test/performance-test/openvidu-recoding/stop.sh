#!/bin/bash

for KILLPID in `ps ax | grep 'worker.sh' | grep -v 'grep' | awk ' { print $1;}'`; do 
	echo $KILLPID;
	kill -9 $KILLPID;
done

for CONTAINERPID in `docker ps | grep openvidu- | awk '{ print $1;}'`; do
	echo $CONTAINERPID;
	docker exec -d $CONTAINERPID bash -c "echo 'q' > stop"
done
