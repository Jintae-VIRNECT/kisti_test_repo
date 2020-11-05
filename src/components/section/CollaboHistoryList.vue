<template>
  <section class="collabo-history-list">
    <div class="collabo-history-list__header">
      <span class="collabo-history-list__header--title">{{
        $t('list.title')
      }}</span>
      <span class="collabo-history-list__header--description">
        {{ $t('list.title_description') }}
      </span>
      <button @click="getExcelData" class="collabo-history-list__header--excel">
        EXCEL
      </button>
    </div>
    <history
      :loading="loading"
      :isMaster="isMaster"
      :historys="historyList"
    ></history>
    <pagination-tool
      @current-page="getHistoryPage"
      :totalPage="pageMeta.totalPage"
    ></pagination-tool>
  </section>
</template>

<script>
import History from 'components/partials/History'
import PaginationTool from 'components/partials/PaginationTool'

import { getHistoryList, getHistorySingleItem } from 'api/http/history'
import {
  getServerRecordFiles,
  getAttachFiles,
  getLocalRecordFiles,
} from 'api/http/file'
import { getMemberInfo } from 'api/http/member'

import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'

import { WORKSPACE_ROLE } from 'configs/status.config'

import { exportExcel } from 'utils/excel'

export default {
  name: 'CollaboHistoryList',
  mixins: [searchMixin, confirmMixin],
  components: {
    History,
    PaginationTool,
  },
  data() {
    return {
      targetId: 0,
      modalVisible: false,
      fileList: [],
      historyList: [],
      isMaster: false,
      pageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      },
      paging: false,
      loading: false,
    }
  },
  computed: {
    // list() {
    //   return this.getFilter(this.historyList, [
    //     'title',
    //     'memberList[].nickName',
    //   ])
    // },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid && val.uuid) {
        this.init()
      }
    },
  },
  methods: {
    async init(page = 0) {
      //check for master
      this.loading = true
      const memberInfo = await getMemberInfo({
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })
      if (memberInfo.role === WORKSPACE_ROLE.MASTER) {
        console.log('role :: ', WORKSPACE_ROLE.MASTER)
        this.isMaster = true
      }

      const list = await this.getHistory(page)
      if (list === false) {
        this.loading = false
        return
      }

      const sorted = list.sort((roomA, roomB) => {
        return (
          new Date(roomB.activeDate).getTime() -
          new Date(roomA.activeDate).getTime()
        )
      })

      await this.addAdditionalData(sorted)

      this.historyList = sorted

      this.loading = false
      this.$eventBus.$emit('scroll:reset:workspace')
    },
    async getHistoryPage(page) {
      this.init(page - 1)
    },
    async getHistory(page = 0) {
      try {
        const datas = await getHistoryList({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
          paging: true,
          page,
        })
        if ('pageMeta' in datas) {
          this.pageMeta = datas.pageMeta
        } else {
          this.pageMeta = {
            currentPage: 0,
            currentSize: 0,
            totalElements: 0,
            totalPage: 0,
            last: false,
          }
        }
        return datas.roomHistoryInfoList
      } catch (err) {
        this.confirmDefault(this.$t('confirm.server_no_response'))
        console.error(err)
        return false
      }
    },
    async setIndex(list) {
      const startIndex = this.pageMeta.currentPage * 7
      let index = 0
      for (const history of list) {
        history.index = index + startIndex
        history.index++
        index++
      }
    },
    async setLeader(list) {
      for (const history of list) {
        const leader = history.memberList.find(member => {
          return member.memberType === 'LEADER'
        })
        history.leader = leader
      }
    },
    async setServerRecord(list) {
      for (const history of list) {
        const datas = await getServerRecordFiles({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          sessionId: history.sessionId,
        })
        history.serverRecord = datas.infos
      }
    },
    async setFile(list) {
      for (const history of list) {
        try {
          const datas = await getAttachFiles({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            sessionId: history.sessionId,
          })

          history.files = datas.fileInfoList.filter(info => !info.deleted)
        } catch (e) {
          history.files = []
          console.error(e)
        }
      }

      console.log(history.files)
      console.log(history.localRecord)
    },
    async setLocalRecord(list) {
      for (const history of list) {
        try {
          const datas = await getLocalRecordFiles({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            sessionId: history.sessionId,
          })
          history.localRecord = datas.fileDetailInfoList
        } catch (e) {
          history.localRecord = []
          console.error(e)
        }
      }

      console.log(history.localRecord)
    },
    async getExcelData() {
      try {
        let merged = []

        const historys = await getHistoryList({
          page: 0,
          paging: false,
          size: 1,
          sort: 'createdDate,desc',
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })

        this.addAdditionalData(historys.roomHistoryInfoList)

        for (const history of historys.roomHistoryInfoList) {
          const room = await getHistorySingleItem({
            workspaceId: this.workspace.uuid,
            sessionId: history.sessionId,
          })
          merged.push({ history, room })
        }

        // 'No,협업명,협업내용,리더,참가자,시작시간,종료시간,진행시간,서버녹화,로컬녹화,첨부파일'
        const header = [
          'No',
          this.$t('excel.room_title'),
          this.$t('excel.room_description'),
          this.$t('excel.room_leader'),
          this.$t('excel.room_member_list'),
          this.$t('excel.room_active_date'),
          this.$t('excel.room_unactive_date'),
          this.$t('excel.room_duration_sec'),
          this.$t('excel.file_server_record'),
          this.$t('excel.file_local_record'),
          this.$t('excel.file_attach_file'),
        ]
        exportExcel(merged, header)
      } catch (err) {
        console.error(err)
        return false
      }
    },
    async addAdditionalData(list) {
      await this.setIndex(list)
      await this.setLeader(list)
      await this.setServerRecord(list)
      await this.setFile(list)
      await this.setLocalRecord(list)
    },
  },

  mounted() {
    this.$eventBus.$on('reload::list', this.init)
    if (this.workspace.uuid) {
      this.init()
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('reload::list')
  },
}
</script>

<style lang="scss">
.collabo-history-list {
  margin-top: 2.9286rem;
}
.collabo-history-list__header {
  position: relative;
  height: 3.8571rem;
}

.collabo-history-list__header--title {
  margin-right: 0.7143rem;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 1.5714rem;
}

.collabo-history-list__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 1.0714rem;
}
.collabo-history-list__header--excel {
  position: absolute;
  right: 0px;
  width: 6.3571rem;
  height: 3rem;
  color: rgb(15, 117, 245);
  font-weight: 500;
  font-size: 1.0714rem;
  line-height: 1.4286rem;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 2px;
  transition: 0.3s;
  &:hover {
    background-color: #f3f3f3;
  }
  &:active {
    background-color: #e6e6e6;
  }
}
</style>
