<template>
  <tab-view
    :title="$t('workspace.history_title')"
    :description="$t('workspace.history_description')"
    :placeholder="$t('workspace.search_room')"
    customClass="history"
    :emptyImage="require('assets/image/img_recent_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :empty="list.length === 0"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="$t('button.remove_all')"
    :listCount="pageMeta.totalElements"
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
        @createRoom="createRoom(history.sessionId, history.sessionType)"
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
      <open-room-modal
        :visible.sync="showOpenRestart"
        :sessionId="sessionId"
      ></open-room-modal>
      <loader :loading="paging"></loader>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import OpenRoomModal from '../modal/WorkspaceCreateOpenRoom'
import HistoryInfoModal from '../modal/WorkspaceHistoryInfo'

import History from 'History'
import Loader from 'Loader'

import searchMixin from 'mixins/filter'
import confirmMixin from 'mixins/confirm'
import { ROOM_STATUS } from 'configs/status.config'

import {
  getHistoryList,
  deleteAllHistory,
  deleteHistorySingleItem,
} from 'api/http/history'

export default {
  name: 'WorkspaceHistory',
  mixins: [searchMixin, confirmMixin],
  components: {
    Loader,
    TabView,
    CreateRoomModal,
    OpenRoomModal,
    History,
    HistoryInfoModal,
  },
  data() {
    return {
      historyList: [],
      loading: false,
      showRestart: false,
      showOpenRestart: false,
      showHistoryInfo: false,
      sessionId: '',
      pageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      },
      paging: false,
    }
  },
  computed: {
    list() {
      return this.getFilter(this.historyList, [
        'title',
        'memberList[].nickName',
      ])
    },
    emptyTitle() {
      if (this.historyList.length > 0) {
        return this.$t('workspace.search_empty')
      } else {
        return this.$t('workspace.history_empty')
      }
    },
    emptyDescription() {
      if (this.historyList.length > 0) {
        return ''
      } else {
        return this.$t('workspace.tab_empty_description')
      }
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
        this.$t('workspace.confirm_remove_room'),
        {
          text: this.$t('button.remove'),
          action: () => {
            this.delete(sessionId)
          },
        },
        { text: this.$t('button.cancel') },
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

      if (result.result) {
        this.pageMeta.totalElements--
        this.confirmDefault(this.$t('workspace.confirm_removed_history'), {
          text: this.$t('button.confirm'),
        })
      }
    },

    //재시작
    async createRoom(sessionId, sessionType) {
      this.sessionId = sessionId
      if (sessionType === ROOM_STATUS.OPEN) {
        this.showOpenRestart = !this.showOpenRestart
      } else {
        this.showRestart = !this.showRestart
      }
    },
    deleteAll() {
      this.confirmCancel(
        this.$t('workspace.confirm_remove_history'),
        {
          text: this.$t('button.remove'),
          action: () => {
            this.deleteList()
          },
        },
        { text: this.$t('button.cancel') },
      )
    },
    async deleteList() {
      try {
        await deleteAllHistory({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        this.historyList = []
        this.pageMeta = {
          currentPage: 0,
          currentSize: 0,
          totalElements: 0,
          totalPage: 0,
          last: false,
        }
      } catch (err) {
        console.error(err)
      }
    },
    async init() {
      this.loading = true
      const list = await this.getHistory()
      if (list === false) {
        this.loading = false
        return
      }
      this.historyList = list.sort((roomA, roomB) => {
        return (
          new Date(roomB.activeDate).getTime() -
          new Date(roomA.activeDate).getTime()
        )
      })
      this.loading = false
      this.$eventBus.$emit('scroll:reset:workspace')
    },
    async moreHistory(event) {
      if (event.bottom !== true) return
      if (
        this.historyList.length === 0 ||
        this.pageMeta.last === true ||
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
        console.error(err)
        return false
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
