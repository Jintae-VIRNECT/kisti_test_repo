<template>
  <div>
    <button @click="connect">Connect Server</button>
    <div>
      <span>connection result : </span>
      <span>{{ minioClient ? 'connected' : 'none' }}</span>
    </div>
    <button @click="getBuckets">Get buckets</button>

    <h2>Bucket list</h2>
    <div v-for="(bucket, index) in buckets" :key="index">
      name :
      <span @click="getObjectList(bucket.name)">{{ bucket.name }} </span>/
      creationDate:
      {{ bucket.creationDate }}
    </div>

    <input type="file" @change="selectFile" />

    <h2>File list</h2>
    <div v-for="(object, index) in objectList" :key="index">
      <br />
      <p>name : {{ object.name }}</p>
      <p>lastModified : {{ object.lastModified }}</p>
      <p>etag : {{ object.etag }}</p>
      <p>size : {{ object.size }}</p>
      <button @click="download(object.name)">download</button>
      <button @click="deleteFile(object.name)">delete</button>
    </div>
  </div>
</template>

<script>
// import Minio from 'minio'
const Minio = require('minio')
export default {
  name: 'DashBoardLayout',
  data() {
    return {
      minioClient: null,
      buckets: [],
      objectList: [],
      currentBucketName: [],
    }
  },
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
    getObjectList(bucketName) {
      this.currentBucketName = bucketName
      this.objectList = []
      var stream = this.minioClient.listObjects(bucketName, '', true)
      stream.on('data', obj => {
        console.log(obj)
        this.objectList.push(obj)
      })
      stream.on('error', err => {
        console.log(err)
      })
    },
    download(fileName) {
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

      // console.log(selectedFile.stream())

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
    deleteFile(fileName) {
      const spt = fileName.split('/')
      console.log(spt[spt.length - 1])
      this.minioClient.removeObject(
        this.currentBucketName,
        spt[spt.length - 1],
        function(err) {
          if (err) {
            return console.log('Unable to remove object', err)
          }
          console.log('Removed the object')
        },
      )
    },
  },
  mounted() {},
}
</script>

<style></style>
