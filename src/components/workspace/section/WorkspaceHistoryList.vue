<template>
  <div class="list-wrapper">
    <history
      v-for="(history, index) in list"
      :key="index"
      height="6.143rem"
      :menu="true"
      :history="history"
      @createRoom="createRoom(history.sessionId)"
      @openRoomInfo="openRoomInfo(history.sessionId)"
      @showDeleteDialog="showDeleteDialog(history.sessionId)"
    ></history>
    <history-info-modal
      :visible.sync="showHistoryInfo"
      :sessionId="sessionId"
    ></history-info-modal>
    <create-room-modal
      :visible.sync="showRestart"
      :sessionId="sessionId"
    ></create-room-modal>
  </div>
</template>

<script>
import History from 'History'

import searchMixin from 'mixins/filter'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import HistoryInfoModal from '../modal/WorkspaceHistoryInfo'

import { deleteHistorySingleItem } from 'api/workspace/history'

import confirmMixin from 'mixins/confirm'
export default {
  name: 'WorkspaceHistoryList',
  mixins: [searchMixin, confirmMixin],
  components: {
    CreateRoomModal,
    History,
    HistoryInfoModal,
  },
  data() {
    return {
      showRestart: false,
      showHistoryInfo: false,
      sessionId: '',
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
    searchFilter() {},
  },
  props: {
    placeholder: {
      type: String,
      default: '',
    },
    historyList: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    //상세보기
    openRoomInfo(sessionId) {
      this.sessionId = sessionId
      this.showHistoryInfo = true
    },
    showDeleteDialog(sessionId) {
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
        sessionId: sessionId,
        userId: this.account.uuid,
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
  },
  created() {},
}
</script>
