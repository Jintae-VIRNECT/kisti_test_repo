## Author

```
delbert@virnect.com
```

## Description

```
compatible with Amazon S3 cloud storage service.
```

## Running the Minio

```
docker run -p 2838:9000 -e "MINIO_ACCESS_KEY=virnect" -e "MINIO_SECRET_KEY=virnect0!" -v /home/virn/minio:/data -d --restart=always --name=pf-minio pf-minio server /data
```
