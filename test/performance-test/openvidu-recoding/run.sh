#!/bin/bash

COUNT=$(seq 1 $2)

for i in $COUNT
do
    ./worker.sh $i $1 &
    sleep 1
done
