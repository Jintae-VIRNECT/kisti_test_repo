#!/bin/bash

export INFLUXDB_URL="http://13.125.24.98:8086/MEDIA"

if [[ -z "${KMS_EXTERNAL_ADDRESS}" ]]; then
  export PUBLIC_IP=$(hostname -I | cut -d' ' -f1)
else
  export PUBLIC_IP="${KMS_EXTERNAL_ADDRESS}"
fi

pm2 start main.js
