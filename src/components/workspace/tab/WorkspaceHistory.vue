<template>
  <tab-view
    :title="$t('workspace.history_title')"
    :placeholder="$t('workspace.search_room')"
    customClass="history"
    :emptyImage="require('assets/image/img_recent_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :empty="list.length === 0"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="$t('button.remove_all')"
    :listCount="
      searchText.length > 0
        ? searchPageMeta.totalElements
        : pageMeta.totalElements
    "
    :loading="loading"
    @search="doSearch"
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

import confirmMixin from 'mixins/confirm'
import { ROOM_STATUS } from 'configs/status.config'
import { nameExp as EXP_NAME } from 'utils/regexp'

import {
  getHistoryList,
  deleteAllHistory,
  deleteHistorySingleItem,
  searchHistoryList,
} from 'api/http/history'

export default {
  name: 'WorkspaceHistory',
  mixins: [confirmMixin],
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
      searchText: '',
      searchHistoryList: [],
      searchPageMeta: {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      },
      searchingText: '',
    }
  },
  computed: {
    list() {
      if (this.searchText.length > 0) {
        return this.searchHistoryList
      } else {
        return this.historyList
      }
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
        if (this.searchText.length > 0) {
          const posSearch = this.searchHistoryList.findIndex(room => {
            return room.sessionId === sessionId
          })
          this.searchHistoryList.splice(posSearch, 1)
        }
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
        this.$eventBus.$emit('scroll:reset:workspace')
      } catch (err) {
        console.error(err)
      }
    },
    async init() {
      this.loading = true
      this.searchText = ''
      this.searchHistoryList = []
      this.searchPageMeta = {
        currentPage: 0,
        currentSize: 0,
        totalElements: 0,
        totalPage: 0,
        last: false,
      }
      this.$eventBus.$emit('search:clear')
      this.historyList = await this.getHistory()
      this.loading = false
      this.$eventBus.$emit('scroll:reset:workspace')
    },
    async doSearch(text) {
      if (this.historyList.length === 0) return
      try {
        if (!EXP_NAME.test(text)) {
          this.searchHistoryList = []
          this.searchPageMeta = {
            currentPage: 0,
            currentSize: 0,
            totalElements: 0,
            totalPage: 0,
            last: false,
          }
          return
        }
        const list = await this.searchHistory(0, text)
        if (!list) return
        this.searchHistoryList = list
        this.searchText = text
        if (!text || text.trim().length === 0) {
          this.searchText = ''
          this.searchHistoryList = []
          this.searchPageMeta = {
            currentPage: 0,
            currentSize: 0,
            totalElements: 0,
            totalPage: 0,
            last: false,
          }
        }
      } catch (err) {
        this.searchText = ''
        this.searchHistoryList = []
        this.searchPageMeta = {
          currentPage: 0,
          currentSize: 0,
          totalElements: 0,
          totalPage: 0,
          last: false,
        }
      }
    },
    async moreHistory(event) {
      if (event.bottom !== true) return
      if (this.searchText.length > 0) {
        if (
          this.searchHistoryList.length === 0 ||
          this.searchPageMeta.last === true ||
          this.paging === true
        )
          return
        this.paging = true
        const list = await this.searchHistory(
          this.searchPageMeta.currentPage + 1,
          this.searchText,
        )
        this.searchHistoryList = this.searchHistoryList.concat(list)
        this.paging = false
      } else {
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
      }
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
        return []
      }
    },
    async searchHistory(page = 0, text) {
      try {
        this.searchingText = text
        const datas = await searchHistoryList({
          paging: true,
          page,
          search: text,
          size: 10,
          sort: 'createdDate,desc',
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })
        if (this.searchingText !== text) return false
        if ('pageMeta' in datas) {
          if (
            page === 0 &&
            datas.pageMeta.totalElements === this.searchPageMeta.totalElements
          ) {
            return this.searchHistoryList
          } else {
            this.searchPageMeta = datas.pageMeta
          }
        } else {
          this.searchPageMeta = {
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
        return []
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
