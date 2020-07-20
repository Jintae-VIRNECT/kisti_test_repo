#!/bin/bash

export GST_DEBUG="3,Kurento*:4,kms*:4,sdp*:4,webrtc*:4,*rtpendpoint:4,rtp*handler:4,rtpsynchronizer:4,agnosticbin:4"

ulimit -n 65535

kurento-media-server --log-file-size=100 --number-log-file=30