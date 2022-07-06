#!/bin/bash

if [[ -z "${INFLUXDB_URL}" ]]; then
  export INFLUXDB_URL="http://192.168.0.9:8086/MEDIA"
fi

if [[ -z "${KMS_EXTERNAL_ADDRESS}" ]]; then
  export PUBLIC_IP=$(hostname -I | cut -d' ' -f1)
else
  export PUBLIC_IP="${KMS_EXTERNAL_ADDRESS}"
fi

pm2 start main.js
