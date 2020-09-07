<template>
  <section class="file-manage">
    <card :title="'파일 관리'">
      <div style="height:100%">
        <button @click="createBucketTEST">testbucket create</button>
        <button @click="deleteBucketTEST">testbucket delete</button>
        <div class="file-manage__header">
          <div class="file-manage__header name">이름</div>
          <div class="file-manage__header size">크기</div>
          <div class="file-manage__header lastmod">최근 수정일</div>
          <div class="file-manage__header tools"></div>
        </div>
        <div class="file-manage__body">
          <scroller height="800px">
            <bucket-list
              v-if="level === 'bucket'"
              :buckets="buckets"
              @selectbucket="loadObjects"
            ></bucket-list>
            <object-list
              v-else
              :objects="objectList"
              @selectobject="loadObjects"
            ></object-list>
          </scroller>
        </div>
      </div>
    </card>
  </section>
</template>

<script>
import Card from 'components/modules/Card'
import BucketList from 'components/list/BucketList'
import ObjectList from 'components/list/ObjectList'
import minioMixin from 'mixins/minio'
import Scroller from 'components/modules/Scroller'
export default {
  name: 'FileManage',
  mixins: [minioMixin],
  components: {
    Card,
    BucketList,
    ObjectList,
    Scroller,
  },
  data() {
    return {
      minioClient: null,
      buckets: [],
      objectList: [],
      currentBucketName: [],
      datas: [],

      level: '', //bucket, object
    }
  },
  methods: {
    loadObjects(bucketName) {
      if (this.level === 'object') {
        this.getObjectList(this.currentBucketName, bucketName)
      } else {
        this.level = 'object'
        this.getObjectList(bucketName)
        this.currentBucketName = bucketName
      }
    },
    createBucketTEST(bucketName) {
      this.createBucket(bucketName)
      console.log(bucketName)
    },
    deleteBucketTEST(bucketName) {
      this.deleteBucket(bucketName)
      console.log(bucketName)
    },
  },
  async mounted() {
    this.connect()
    this.level = 'bucket'
    await this.getBuckets()

    this.$eventBus.$on('delete::object', this.deleteObject)
    this.$eventBus.$on('download::object', this.downloadObject)
    this.$eventBus.$on('delete::bucket', this.deleteBucket)
  },
  beforeDestroy() {
    this.$eventBus.$off('delete::object')
    this.$eventBus.$off('download::object')
    this.$eventBus.$off('delete::bucket')
  },
}
</script>

<style lang="scss">
.file-manage__header {
  display: flex;

  margin-top: 10px;
  padding-bottom: 5px;
  font-weight: 500;
  font-size: 15px;

  &.name {
    width: 1000px;
  }

  &.size {
    width: 200px;
    text-align: center;
  }

  &.lastmod {
    width: 200px;
    text-align: center;
  }

  &.tools {
    width: 200px;
  }
}

.file-manage__body {
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}
.file-manage__row {
  display: flex;
  align-items: center;
  height: 50px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
  border-bottom: 1px solid #3d3f51;

  &.name {
    width: 1000px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;

    &:hover {
      cursor: pointer;
    }
  }

  &.size {
    width: 200px;
    text-align: center;
  }

  &.lastmod {
    width: 200px;
    text-align: center;
  }

  &.tools {
    width: 200px;
  }
}
</style>
