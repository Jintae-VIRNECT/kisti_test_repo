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

import { getRecordFiles } from 'api/remote/record'

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
      fileList: [],
    }
  },

  methods: {
    showList(serialNum) {
      console.log(serialNum)
      this.modalVisible = true
    },
    async getRecordList(userId) {
      console.log(userId)
      const params = {}
      const datas = await getRecordFiles(params)
      this.fileList = datas.infos
    },
  },

  async mounted() {
    //세션명? 유저명 기반 요청 필요함

    this.getRecordList(1234)

    this.$eventBus.$on('open::record-list', this.showList)
    this.$eventBus.$on('load::record-list', this.getRecordList)
  },
  beforeDestroy() {
    this.$eventBus.$off('open::record-list')
    this.$eventBus.$off('load::record-list')
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
