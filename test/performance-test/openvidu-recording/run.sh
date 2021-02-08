#!/bin/bash

function show_help {
    echo "Usage: run.sh -r RESOLUTION -l LAYOUT_URL -n SESSIONS"
}

while getopts "h?r:n:l:" opt; do
    case "$opt" in
    h|\?)
        show_help
        exit 0
        ;;
    r)  RESOLUTION=$OPTARG
        ;;
    n)  SESSIONS=$OPTARG
        ;;
    l)  LAYOUT_URL=$OPTARG
        ;;
    esac
done

echo "SESSIONS: $SESSIONS"
echo "RESOLUTION: $RESOLUTION"
echo "LAYOUT_URL: $LAYOUT_URL"

for i in $(seq 1 $SESSIONS)
do
    ./worker.sh $i $RESOLUTION $LAYOUT_URL &
    sleep 1
done
