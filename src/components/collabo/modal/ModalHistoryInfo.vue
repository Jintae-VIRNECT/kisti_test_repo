<template>
  <modal
    :title="room.title"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    width="58.571rem"
    height="51.8571rem"
    :showClose="true"
    customClass="modal-history-info"
  >
    <p class="history-info--header">{{ $t('room.room_info') }}</p>
    <div class="history-info-table">
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.title') }}
        </div>
        <div class="history-info-table-column__info">
          <span>{{ room.title }}</span>
          <collabo-status
            :status="localStatus"
            :customClass="'custom-collabo-status'"
          ></collabo-status>
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.description') }}
        </div>
        <div class="history-info-table-column__info">
          <p>{{ room.description }}</p>
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.leader') }}
        </div>
        <div class="history-info-table-column__info">
          {{ leader.nickName }}
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.member_list') }}
        </div>
        <div class="history-info-table-column__info member-list">
          <profile-list
            :users="room.memberList"
            :customStyle="{
              width: '2.7143rem',
              height: '2.7143rem',
              'font-size': '1rem',
              'margin-left': '0.8571rem',
            }"
            size="2.7143rem"
          ></profile-list>
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.active_date') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ activeDate }}
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.unactive_date') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ unactiveDate }}
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.duration_sec') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ durationSec }}
        </div>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileList from 'ProfileList'
import CollaboStatus from 'CollaboStatus'
import { getHistorySingleItem, getRoomInfo } from 'api/http/history'

import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
export default {
  name: 'ModalHistoryInfo',
  components: {
    Modal,
    ProfileList,
    CollaboStatus,
  },
  filters: {
    dateTimeFormat(dateTime) {
      return dateTimeFormat(dateTime)
    },
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    status: {
      type: Boolean,
      default: true,
    },
    sessionId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      room: {},
      visibleFlag: false,
      localStatus: true,
    }
  },
  computed: {
    leader() {
      return this.room.memberList
        ? this.room.memberList.find(member => {
            return member.memberType === 'LEADER'
          })
        : ''
    },
    activeDate() {
      return this.room.activeDate ? dateTimeFormat(this.room.activeDate) : ''
    },
    unactiveDate() {
      return this.room.unactiveDate
        ? dateTimeFormat(this.room.unactiveDate)
        : ''
    },
    durationSec() {
      return this.room.durationSec ? durationFormat(this.room.durationSec) : ''
    },
  },
  watch: {
    visible(flag) {
      if (flag === true) {
        this.initHistory()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    async initHistory() {
      this.room = {}
      let info = null
      this.localStatus = this.status

      if (this.status) {
        info = await this.getHistory()
      } else {
        info = await this.getRoom()
        if (info === null) {
          info = await this.getHistory()
          this.localStatus = true
        }
      }
      this.room = info
    },
    async getRoom() {
      let result = null
      try {
        result = await getRoomInfo({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
      } catch (e) {
        console.error(e)
        if (e.code === 4002) {
          //@TODO : MSG
          console.error('room closed')
        }
      }
      return result
    },
    async getHistory() {
      let result = null
      try {
        result = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
      } catch (err) {
        console.error(err)
        throw err
      }
      return result
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },
}
</script>
