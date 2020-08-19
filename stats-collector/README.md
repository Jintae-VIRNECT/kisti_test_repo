# Stats Collector

## forever

```bash
npm install forever -g
```

## Run

```bash
INFLUXDB_URL="http://13.125.24.98:8086/MEDIA" NODE_ENV="production" KMS_EXTERNAL_ADDRESS=1.1.1.1 forever start -l forever.log -o out.log -e err.log main.js
```

## Stop

```bash
forever stop main.js
```
