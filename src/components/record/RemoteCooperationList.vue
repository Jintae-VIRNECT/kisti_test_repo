<template>
  <div>
    <section class="record-list__header">
      <h1 class="record-list__header--title">원격 협업 내역</h1>
      <p class="record-list__header--description">
        사용자의 원격협업 내역을 확인할 수 있습니다.
      </p>
    </section>
    <search-bar></search-bar>
    <user-table :showList="showList"></user-table>
    <pagination-tool :totalPage="5"></pagination-tool>
    <!-- 테이블 -->
    <!-- 페이지네이션 툴 -->
    <record-file-list
      :visible.sync="modalVisible"
      :fileList="fileList"
    ></record-file-list>
  </div>
</template>

<script>
import SearchBar from './RemoteSearchBar'
import UserTable from './RemoteUserTable'
import PaginationTool from './partials/RemotePaginationTool'
import RecordFileList from './RemoteRecordFileList'
// import Modal from 'components/modules/Modal'
export default {
  name: 'RemoteCooperationList',
  components: {
    SearchBar,
    UserTable,
    PaginationTool,
    RecordFileList,
  },

  data() {
    return {
      targetId: 0,
      modalVisible: false,
      fileList: [
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
        {
          filename: '2020-06-11 HH-MM-SS_00.mp4​',
          recordingId: '123456',
          duration: 30,
          size: 5590847,
        },
      ],
    }
  },

  methods: {
    showList(serialNum) {
      console.log(serialNum)
      this.modalVisible = true
    },
  },

  mounted() {
    this.$eventBus.$on('open::record-list', this.showList)
  },
  beforeDestroy() {
    this.$eventBus.$off('open::record-list')
  },

  // 유저 목록 호출 및 테이블 갱신
}
</script>

<style lang="scss">
.record-list--header {
}

.record-list__header--title {
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 32px;
}

.record-list__header--description {
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 16px;
  line-height: 20px;
}
</style>
