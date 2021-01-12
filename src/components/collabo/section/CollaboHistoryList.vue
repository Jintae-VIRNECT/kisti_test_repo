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
        @click="createExcel"
        class="collabo-history-list__header--excel"
        :class="excelBtnClass"
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
      @current-page="getHistoryByPage"
      :totalPage="pageMeta.totalPage"
      :currentPage="pageMeta.currentPage"
    ></pagination-tool>
  </section>
</template>

<script>
import History from 'components/collabo/partials/CollaboHistory'
import PaginationTool from 'components/collabo/partials/CollaboPaginationTool'

import { getHistoryList, getAllHistoryList } from 'api/http/history'
import { getMemberInfo } from 'api/http/member'

import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/search'

import { WORKSPACE_ROLE } from 'configs/status.config'
import { ROLE } from 'configs/remote.config'
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
      isMaster: false,

      historyList: [],
      curPage: 0,
      paging: false,
      pageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      },
      loading: false,
      excelLoading: false,
    }
  },
  computed: {
    ...mapGetters(['searchFilter']),
    excelBtnClass() {
      return [
        { 'excel-loading': this.excelLoading },
        {
          disable: this.historyList.length <= 0,
        },
      ]
    },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid && val.uuid) {
        this.init()
      }
    },
  },
  methods: {
    async getHistoryByPage(page) {
      await this.init(page - 1)
    },

    async init(page = 0) {
      this.loading = true

      const memberInfo = await getMemberInfo({
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      if (memberInfo.role === WORKSPACE_ROLE.MASTER) {
        this.isMaster = true
      }

      const paging = true
      const list = await this.getHistory(paging, page)
      if (list === false) {
        this.loading = false
        return
      }

      this.historyList = list
      this.loading = false
    },

    async getHistory(paging, page = 0) {
      try {
        const params = this.getParams(paging, page)
        const datas = await this.getData(params)

        if (paging) {
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
        }

        return datas.roomHistoryInfoList
      } catch (err) {
        this.confirmDefault(this.$t('confirm.server_no_response'))
        console.error(err)
        return false
      }
    },

    async createExcel() {
      try {
        if (this.historyList.length <= 0 || this.excelLoading) return

        this.excelLoading = true

        const paging = false
        const page = 0
        const historys = await this.getHistory(paging, page)

        if (!historys) {
          return
        }

        historys.forEach(history => {
          if (history.memberList && history.memberList.length > 0) {
            const leader = history.memberList.find(member => {
              return member.memberType === ROLE.LEADER
            })

            if (leader && leader.nickName) {
              history.leaderNickName = leader.nickName
            } else {
              history.leaderNickName = ''
            }
          } else {
            history.leaderNickName = ''
          }
        })

        exportExcel(historys, this)

        this.excelLoading = false
      } catch (err) {
        console.error(err)
        this.excelLoading = false
      }
    },

    async getData(params) {
      return this.isMaster
        ? await getAllHistoryList(params)
        : await getHistoryList(params)
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
