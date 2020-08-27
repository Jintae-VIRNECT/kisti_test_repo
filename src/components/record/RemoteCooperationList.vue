<template>
  <div>
    <section class="record-list__header">
      <h1 class="record-list__header--title">원격 협업 내역</h1>
      <p class="record-list__header--description">
        사용자의 원격협업 내역을 확인할 수 있습니다.
      </p>
    </section>
    <search-bar></search-bar>
    <user-table :userList="userList"></user-table>
    <pagination-tool
      :totalPage="totalPage"
      @currentPage="currentPage"
    ></pagination-tool>
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
      userList: [
        {
          index: 1,
          userName:
            '일이삼사아육칠팔구십일이삼사아육칠팔구십일이삼사아육칠팔구',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 123456,
        },
        {
          index: 2,
          userName: 'test1',
          cooperateName:
            '일이삼사아육칠팔구십일이삼사아육칠팔구asgfasfgsafssdsfdsfdfdsdsfdsfdsfdsfdfdsfdsfdsfddsfsdfafs십',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 12342134,
        },
        {
          index: 3,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 65467543,
        },
        {
          index: 4,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 264565196,
        },
        {
          index: 5,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 0,
          serialNum: 745864123,
        },
        {
          index: 6,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 999,
          serialNum: 1215,
        },
        {
          index: 7,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 2,
          serialNum: 4984,
        },
        {
          index: 8,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 4567984,
        },
      ],

      totalPage: 13,
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
    currentPage(pageNum) {
      console.log(pageNum)
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
