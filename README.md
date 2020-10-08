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
Set MINIO_ACCESS_KEY & MINIO_SECRET_KEY 

Ex)
docker run -p 2838:9000 -e "MINIO_ACCESS_KEY=virnect" -e "MINIO_SECRET_KEY=Qjsprxm13@$" -v /data/minio:/data -d --restart=always --name=pf-minio pf-minio server /data

```
