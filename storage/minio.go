package storage

import (
	"RM-RecordServer/data"
	"context"
	"crypto/tls"
	"fmt"
	"net/http"
	"net/url"
	"os"
	"path"
	"sync"
	"time"

	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type Client struct {
	minioClient  *minio.Client
	bucketName   string
	resourceName string
}

var once sync.Once
var client *Client

func GetClient() *Client {
	once.Do(func() {
		client = New()
	})
	return client
}

func Init() {
	ctx := context.Background()
	bucketName := viper.GetString("storage.bucketName")
	client := GetClient()

	found, err := client.minioClient.BucketExists(ctx, bucketName)
	if err != nil {
		panic(err)
	}

	if !found {
		err = client.minioClient.MakeBucket(ctx, bucketName, minio.MakeBucketOptions{ObjectLocking: false, Region: ""})
		if err != nil {
			panic(err)
		}
		logrus.Info("create bucket:", bucketName)
	}

	// policy := `{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetBucketLocation","s3:ListBucket"],"Resource":["arn:aws:s3:::` + bucketName + `"]},{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::` + bucketName + `/*"]}]}`
	// err = client.minioClient.SetBucketPolicy(ctx, bucketName, policy)
	// if err != nil {
	// 	panic(err)
	// }
}

func New() *Client {
	endpoint := viper.GetString("storage.endpoint")
	accessKey := viper.GetString("storage.accessKey")
	secretKey := viper.GetString("storage.secretKey")
	useSSL := viper.GetBool("storage.useSSL")

	opt := &minio.Options{
		Creds:  credentials.NewStaticV4(accessKey, secretKey, ""),
		Secure: useSSL,
	}
	if useSSL == true {
		opt.Transport = &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}}
	}

	minoClient, err := minio.New(endpoint, opt)
	if err != nil {
		panic(err)
	}
	return &Client{
		minioClient:  minoClient,
		bucketName:   viper.GetString("storage.bucketName"),
		resourceName: viper.GetString("storage.resourceName"),
	}
}

func (c *Client) getObjectName(target string) string {
	return path.Join(c.resourceName, target)
}

func (c *Client) Upload(ctx context.Context, src string, target string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	file, err := os.Open(src)
	if err != nil {
		log.Error("upload:", err)
		return err
	}
	defer file.Close()

	fileStat, err := file.Stat()
	if err != nil {
		log.Error("upload:", err)
		return err
	}

	_, err = c.minioClient.PutObject(
		ctx,
		c.bucketName,
		c.getObjectName(target),
		file,
		fileStat.Size(),
		minio.PutObjectOptions{
			ContentType: "application/octet-stream",
		},
	)
	if err != nil {
		log.Error("upload:", err)
		return err
	}

	log.Info("successfully upload:", src)
	return nil
}

func (c *Client) Remove(ctx context.Context, target string) error {
	return c.minioClient.RemoveObject(ctx, c.bucketName, c.getObjectName(target), minio.RemoveObjectOptions{})
}

func (c *Client) GetPresignedUrl(ctx context.Context, target string, filename string) (string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	key := "response-content-disposition"
	value := fmt.Sprintf("attachment; filename=\"%s\"", filename)
	reqParams := make(url.Values)
	reqParams.Set(key, value)

	expirationTime := time.Second * 24 * 60 * 60 // 1 day
	presignedURL, err := c.minioClient.PresignedGetObject(context.Background(), c.bucketName, c.getObjectName(target), expirationTime, reqParams)
	if err != nil {
		log.Error(err)
		return "", err
	}

	return presignedURL.String(), nil
}

func (c *Client) GetObjectUrl(ctx context.Context, target string, filename string) (string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	useSSL := viper.GetBool("storage.useSSL")
	endpoint := viper.GetString("storage.endpoint")
	u := new(url.URL)
	if useSSL {
		u.Scheme = "https"
	} else {
		u.Scheme = "http"
	}
	u.Path = path.Join(endpoint, c.bucketName, c.resourceName, target)
	log.Info("download url:", u.String())
	return u.String(), nil
}
