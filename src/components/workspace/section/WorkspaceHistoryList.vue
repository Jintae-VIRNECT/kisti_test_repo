<template>
  <div class="list-wrapper">
    <history
      v-for="(history, index) in list"
      :key="index"
      height="6.143rem"
      :menu="true"
      :history="history"
    ></history>
    <roominfo-modal
      :visible.sync="showRoomInfo"
      :roomId="roomId"
    ></roominfo-modal>
    <create-room-modal
      :visible.sync="showRestart"
      :roomId="roomId"
    ></create-room-modal>
    <device-denied :visible.sync="showDenied"></device-denied>
  </div>
</template>

<script>
import History from 'History'

import searchMixin from 'mixins/filter'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import RoominfoModal from '../../workspace/modal/WorkspaceRoomInfo'
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import { getPermission } from 'utils/deviceCheck'

import { deleteHistorySingleItem } from 'api/workspace/history'
import { getLicense } from 'api/workspace/license'

import confirmMixin from 'mixins/confirm'
export default {
  name: 'WorkspaceHistoryList',
  mixins: [searchMixin, confirmMixin],
  components: {
    CreateRoomModal,
    History,
    RoominfoModal,
    DeviceDenied,
  },
  data() {
    return {
      showRestart: false,
      showRoomInfo: false,
      roomId: 0,
      showDenied: false,
    }
  },
  computed: {
    list() {
      return this.getFilter(this.historyList, [
        'title',
        'participants[].nickname',
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
    openRoomInfo(roomId) {
      this.roomId = roomId
      this.showRoomInfo = true
    },
    showDeleteDialog(roomId) {
      this.$eventBus.$emit('popover:close')

      this.confirmCancel(
        '협업을 삭제 하시겠습니까?',
        {
          text: '삭제하기',
          action: () => {
            this.delete(roomId)
            this.confirmDefault('협업을 삭제하였습니다.​', { text: '확인' })
          },
        },
        { text: '취소' },
      )
    },
    async delete(roomId) {
      this.$nextTick(() => {
        const pos = this.historyList.findIndex(room => {
          return room.roomId === roomId
        })
        this.historyList.splice(pos, 1)
      })
      await deleteHistorySingleItem({ roomId })
    },

    //재시작
    async createRoom(roomId) {
      const license = await getLicense(
        this.workspace.uuid,
        await this.account.uuid,
      )

      if (!license) {
        this.confirmDefault('라이선스가 만료되어 서비스 사용이 불가 합니다.​', {
          text: '확인',
          action: () => {
            this.$eventBus.$emit('showLicensePage')
          },
        })
        return false
      }

      this.roomId = roomId
      this.showRestart = !this.showRestart

      const permission = await getPermission()
      if (!permission && this.showRestart === true) {
        this.showDenied = true
      }
    },
    convertDate(date) {
      if (date !== null && date !== '') {
        const re = /T/gi
        let cvtDate = date.replace(re, ' ')
        cvtDate = this.$dayjs(cvtDate).format('YYYY.MM.DD')
        const today = this.$dayjs().format('YYYY.MM.DD')
        if (cvtDate === today) {
          return 'Today'
        } else {
          return cvtDate
        }
      } else {
        this.logger('WorkspaceHistoryList :: convertDate ::', date)
      }
    },
    convertTime(totalUseTime) {
      const min = Math.floor(totalUseTime / 60)
      const minText = '분'
      const sec = totalUseTime % 60
      const secText = '초'
      return `${min + minText + ' ' + sec + secText}`
    },
  },
  created() {},
}
</script>
