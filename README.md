# Remote-CoturnServer

## Start
```
docker-compose -f docker-compose.yml up --build --detach
```

## Restart coturn server
Notice: May restart needed for coturn container, if it could not access database yet, due initialization delay.
```
docker restart docker_coturn_1
```

## Stop all
```
docker-compose -f docker-compose.yml down
```

## Add/Remove admin user
```
# add
docker exec -it turn_coturn_1 turnadmin -A -M "host=mysql dbname=coturn user=remote password=123456" -u admin -p 123456 -r test

# remove
docker exec -it turn_coturn_1 turnadmin -D -M "host=mysql dbname=coturn user=remote password=123456" -u remote -p 123456 -r test
```
## Add/Remove long-term mechanism user
```
# add
docker exec -it turn_coturn_1 turnadmin -a -M "host=mysql dbname=coturn user=remote password=123456" -u remote -p 123456 -r test

# remove
docker exec -it turn_coturn_1 turnadmin -d -M "host=mysql dbname=coturn user=remote password=123456" -u remote -p 123456 -r test
```
