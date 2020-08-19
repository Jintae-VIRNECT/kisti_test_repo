#!/bin/bash

## run stats-collector script
cd /stats-collector && ./run.sh &

## run kurento media server
/entrypoint.sh