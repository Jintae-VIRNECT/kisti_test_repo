# Remote-CoturnServer

## Run Coturn

```
docker run -d -rm --name turn \
    -v ${PWD}/coturn/turnserver.conf:/etc/turnserver.conf \
    -v ${PWD}/coturn/private.pem:/etc/ssl/private.pem \
    -v ${PWD}/coturn/cert.pem:/etc/ssl/cert.pem \
    -p 4443:4443 \
    -p 3478:3478 \
    -p 3478:3478/udp \
    -p 5349:5349 \
    -p 5349:5349/udp \
    -p 50000-50100:50000-50100 \
    -p 50000-50100:50000-50100/udp \
    remote-turn --external-ip=<aws-eip> --mysql-userdb=<db-conn-string>
```

## Run Coturn w/ Mysq for Local Test

### Start

```
docker-compose -f docker-compose.yml up --build --detach
```

### Restart coturn server

Notice: May restart needed for coturn container, if it could not access database yet, due initialization delay.

```
docker restart docker_coturn_1
```

### Stop all

```
docker-compose -f docker-compose.yml down
```

## Examples

### Add/Remove admin user

```
# add
docker exec -it rm-coturnserver_coturn_1 turnadmin -A -M "host=platform-qa-db.cxb5wiohfeko.ap-northeast-2.rds.amazonaws.com dbname=coturn user=virnect-coturn password=virnect-1234" -u admin -p admin -r remote

# remove
docker exec -it turn_coturn_1 turnadmin -D -M "host=mysql dbname=coturn user=remote password=123456" -u remote -p 123456 -r test
```

### Add/Remove long-term mechanism user

```
# add
docker exec -it rm-coturnserver_coturn_1 turnadmin -a -M "host=mysql dbname=coturn user=virnect-coturn password=virnect-1234" -u remote -p remote -r remote

# remove
docker exec -it turn_coturn_1 turnadmin -d -M "host=mysql dbname=coturn user=remote password=123456" -u remote -p 123456 -r test
```
