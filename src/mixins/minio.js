const Minio = require('minio')
export default {
  methods: {
    connect() {
      this.minioClient = new Minio.Client({
        endPoint: '192.168.13.36',
        port: 9000,
        useSSL: false,
        accessKey: 'admin',
        secretKey: '12345678',
      })
    },
    getBuckets() {
      this.minioClient.listBuckets((err, buckets) => {
        if (err) return console.log(err)
        this.buckets = buckets
        console.log('buckets :', buckets)
      })
    },
    getObjectList(bucketName, prefix = '') {
      this.objectList = []
      var stream = this.minioClient.listObjects(bucketName, prefix, false)
      stream.on('data', obj => {
        console.log(obj)
        this.objectList.push(obj)
      })
      stream.on('error', err => {
        console.log(err)
      })
    },
    downloadObject(fileName) {
      var size = 0
      const chunks = []
      this.minioClient.getObject(
        this.currentBucketName,
        fileName,
        (err, dataStream) => {
          if (err) {
            return console.log(err)
          }
          dataStream.on('data', function(chunk) {
            size += chunk.length
            chunks.push(chunk)
          })
          dataStream.on('end', function() {
            console.log('End. Total size = ' + size)
            console.log('chunk::', chunks)
            const blobs = new Blob([chunks[0]])

            const spt = fileName.split('/')

            var downloadUrl = URL.createObjectURL(blobs)
            var a = document.createElement('a')
            a.href = downloadUrl
            a.download = spt[spt.length - 1]
            document.body.appendChild(a)
            a.click()
          })
          dataStream.on('error', function(err) {
            console.log(err)
          })
        },
      )
    },
    selectFile(event) {
      console.log(event.target.files)
      const selectedFile = event.target.files[0]

      this.minioClient.putObject(
        this.currentBucketName,
        selectedFile.name,
        selectedFile.stream(),
        selectedFile.size,
        (error, etag) => {
          console.log(error, etag)
        },
      )
    },
    deleteObject(fileName) {
      this.minioClient.removeObject(this.currentBucketName, fileName, function(
        err,
      ) {
        if (err) {
          return console.log('Unable to remove object', err)
        }
        console.log('Removed the object')
      })
    },
    deleteBucket() {
      this.minioClient.removeBucket('testbbb', function(err) {
        if (err) return console.log('unable to remove bucket.')
        console.log('Bucket removed successfully.')
      })
    },
    createBucket() {
      this.minioClient.makeBucket('testbbb', '', function(err) {
        if (err) return console.log('Error creating bucket.', err)
        console.log('Bucket created successfully')
      })
    },
  },
}
