<template>
  <section class="collabo-history-list">
    <div class="collabo-history-list__header">
      <span class="collabo-history-list__header--title">{{
        $t('list.title')
      }}</span>
      <span class="collabo-history-list__header--description">
        {{ $t('list.title_description') }}
      </span>
      <button
        @click="getExcelData"
        class="collabo-history-list__header--excel"
        :class="[
          { 'excel-loading': excelLoading },
          {
            disable: historyList.length <= 0,
          },
        ]"
      >
        <p v-if="!excelLoading">EXCEL</p>
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
import History from 'components/collabo/partials/CollaboHistory'
import PaginationTool from 'components/collabo/partials/CollaboPaginationTool'

import {
  getHistoryList,
  getAllHistoryList,
  getHistorySingleItem,
} from 'api/http/history'

import { getMemberInfo } from 'api/http/member'

import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/search'

import { WORKSPACE_ROLE } from 'configs/status.config'

import { exportExcel } from 'utils/excel'

import { mapGetters } from 'vuex'

export default {
  name: 'CollaboHistoryList',
  mixins: [searchMixin, confirmMixin],
  components: {
    History,
    PaginationTool,
  },
  data() {
    return {
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
      excelLoading: false,
    }
  },
  computed: {
    ...mapGetters(['searchFilter']),
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

    async getHistory(page = 0) {
      try {
        const paging = true

        const datas = this.isMaster
          ? await getAllHistoryList(this.getParams(paging, page))
          : await getHistoryList(this.getParams(paging, page))

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

    async getExcelData() {
      try {
        if (this.historyList.length <= 0 || this.excelLoading) return

        this.excelLoading = true
        let merged = []

        const paging = false
        const params = this.getParams(paging, 0)

        const historys = this.isMaster
          ? await getAllHistoryList(params)
          : await getHistoryList(params)

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
        this.excelLoading = false
      } catch (err) {
        console.error(err)
        this.excelLoading = false
      }
    },
    async addAdditionalData(list) {
      await this.setIndex(list)
      await this.setLeader(list)
    },
    async getHistoryPage(page) {
      this.init(page - 1)
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
