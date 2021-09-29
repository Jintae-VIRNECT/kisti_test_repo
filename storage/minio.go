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
	"strings"
	"sync"
	"time"

	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type Client struct {
	MinioClient  *minio.Client
	Endpoint     string
	UseSSL       bool
	BucketName   string
	ResourceName string
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

	found, err := client.MinioClient.BucketExists(ctx, bucketName)
	if err != nil {
		panic(err)
	}

	if !found {
		err = client.MinioClient.MakeBucket(ctx, bucketName, minio.MakeBucketOptions{ObjectLocking: false, Region: ""})
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
		MinioClient:  minoClient,
		BucketName:   viper.GetString("storage.bucketName"),
		ResourceName: viper.GetString("storage.resourceName"),
		UseSSL:       useSSL,
		Endpoint:     endpoint,
	}
}

func (c *Client) getObjectName(target string) string {
	//return path.Join(c.ResourceName, target)
	return target
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

	_, err = c.MinioClient.PutObject(
		ctx,
		c.BucketName,
		c.getObjectName(target),
		file,
		fileStat.Size(),
		minio.PutObjectOptions{
			ContentType:  "video/*",
			UserMetadata: map[string]string{"x-amz-acl": "public-read"},
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
	return c.MinioClient.RemoveObject(ctx, c.BucketName, c.getObjectName(target), minio.RemoveObjectOptions{})
}

func (c *Client) GetPresignedUrl(ctx context.Context, target string, filename string) (string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	key := "response-content-disposition"
	value := fmt.Sprintf("attachment; filename=\"%s\"", filename)
	reqParams := make(url.Values)
	reqParams.Set(key, value)

	expirationTime := time.Second * 24 * 60 * 60 // 1 day
	presignedURL, err := c.MinioClient.PresignedGetObject(context.Background(), c.BucketName, c.getObjectName(target), expirationTime, reqParams)
	if err != nil {
		log.Error(err)
		return "", err
	}

	return presignedURL.String(), nil
}

func (c *Client) GetObjectUrl(ctx context.Context, target string) (string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	u := new(url.URL)
	if c.UseSSL {
		u.Scheme = "https"
	} else {
		u.Scheme = "http"
	}
	if strings.Contains(c.Endpoint, "amazonaws.com") {
		u.Host = strings.Join([]string{c.BucketName, c.Endpoint}, ".")
		u.Path = path.Join(c.ResourceName, target)
	} else {
		u.Host = c.Endpoint
		u.Path = path.Join(c.BucketName, c.ResourceName, target)
	}
	log.Info("download url:", u.String())
	return u.String(), nil
}
