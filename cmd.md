
docker build --tag rm-recordserver . -f docker/Dockerfile

docker run -it --rm -p 8083:8083 `
    -v /var/run/docker.sock:/var/run/docker.sock `
    -v C:\Users\k2h\RM-RecordServer\recordings:/recordings `
    -v C:\Users\k2h\RM-RecordServer\config.win.ini:/app/config.ini `
    --name recordserver rm-recordserver

docker run -it --rm -p 8083:8083 `
    -v /var/run/docker.sock:/var/run/docker.sock `
    -v //c/Users/k2h/RM-RecordServer/recordings:/recordings `
    -v //c/Users/k2h/RM-RecordServer/config.win.ini:/app/config.ini `
    --name recordserver rm-recordserver


docker build --tag httpserver .
docker run -it --rm -p 9000:9000 --name httpserver httpserver
