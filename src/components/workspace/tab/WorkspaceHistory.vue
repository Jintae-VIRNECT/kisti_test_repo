<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="협업, 멤버 이름 검색"
    customClass="history"
    :emptyImage="require('assets/image/img_recent_empty.svg')"
    emptyTitle="최근 통화 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :empty="historyList.length === 0"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="historyList.length"
    :loading="loading"
    @refresh="init"
    @delete="deleteAll"
  >
    <div class="list-wrapper">
      <history
        v-for="(history, index) in list"
        :key="index"
        height="6.143rem"
        :menu="true"
        :history="history"
        @createRoom="createRoom(history.sessionId)"
        @openRoomInfo="openRoomInfo(history.sessionId)"
        @deleteHistory="deleteHistory(history.sessionId)"
      ></history>
      <history-info-modal
        :visible.sync="showHistoryInfo"
        :sessionId="sessionId"
      ></history-info-modal>
      <create-room-modal
        :visible.sync="showRestart"
        :sessionId="sessionId"
      ></create-room-modal>
      <loader :loading="paging"></loader>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'

import History from 'History'
import Loader from 'Loader'

import searchMixin from 'mixins/filter'
import confirmMixin from 'mixins/confirm'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import HistoryInfoModal from '../modal/WorkspaceHistoryInfo'

import {
  getHistoryList,
  deleteAllHistory,
  deleteHistorySingleItem,
} from 'api/workspace'

export default {
  name: 'WorkspaceHistory',
  mixins: [searchMixin, confirmMixin],
  components: {
    Loader,
    TabView,
    CreateRoomModal,
    History,
    HistoryInfoModal,
  },
  data() {
    return {
      historyList: [],
      loading: false,
      showRestart: false,
      showHistoryInfo: false,
      sessionId: '',
      pageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
      },
      paging: false,
    }
  },
  computed: {
    list() {
      return this.getFilter(this.historyList, [
        'title',
        'memberList[].nickname',
      ])
    },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid && val.uuid) {
        this.init()
      }
    },
    searchFilter() {},
  },
  methods: {
    //상세보기
    openRoomInfo(sessionId) {
      this.$eventBus.$emit('popover:close')
      this.sessionId = sessionId
      this.showHistoryInfo = true
    },
    deleteHistory(sessionId) {
      this.$eventBus.$emit('popover:close')

      this.confirmCancel(
        '협업을 삭제 하시겠습니까?',
        {
          text: '삭제하기',
          action: () => {
            this.delete(sessionId)
          },
        },
        { text: '취소' },
      )
    },
    async delete(sessionId) {
      this.$nextTick(() => {
        const pos = this.historyList.findIndex(room => {
          return room.sessionId === sessionId
        })
        this.historyList.splice(pos, 1)
      })

      const result = await deleteHistorySingleItem({
        workspaceId: this.workspace.uuid,
        sessionIdList: [sessionId],
        uuid: this.account.uuid,
      })

      if (result.data) {
        this.confirmDefault('협업을 삭제하였습니다.​', { text: '확인' })
      }
    },

    //재시작
    async createRoom(sessionId) {
      this.sessionId = sessionId
      this.showRestart = !this.showRestart
    },
    deleteAll() {
      this.confirmCancel(
        '모든 목록을 삭제하시겠습니까?',
        {
          text: '삭제하기',
          action: () => {
            this.deleteList()
          },
        },
        { text: '취소' },
      )
    },
    async deleteList() {
      try {
        await deleteAllHistory({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        this.historyList = []
      } catch (err) {
        console.error(err)
      }
    },
    async init() {
      this.loading = true
      const list = await this.getHistory()
      this.historyList = list
      this.loading = false
      this.$eventBus.$emit('scroll:reset')
    },
    async moreHistory(event) {
      if (event.bottom !== true) return
      if (
        this.historyList.length === 0 ||
        this.pageMeta.currentPage === this.pageMeta.totalPage ||
        this.paging === true
      )
        return
      this.paging = true
      const list = await this.getHistory(this.pageMeta.currentPage + 1)
      this.historyList = this.historyList.concat(list)
      this.paging = false
    },
    async getHistory(page = 0) {
      try {
        const datas = await getHistoryList({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
          paging: false,
          page,
        })
        this.pageMeta = datas.pageMeta
        return datas.roomHistoryInfoList
      } catch (err) {
        console.error(err)
      }
    },
  },

  mounted() {
    if (this.workspace.uuid) {
      this.init()
    }
    this.$eventBus.$on('scroll:end', this.moreHistory)
  },
  beforeDestroy() {
    this.$eventBus.$off('scroll:end', this.moreHistory)
  },
}
</script>
