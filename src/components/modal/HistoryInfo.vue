<template>
  <modal
    :title="room.title"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    width="819.994px"
    height="725.9994px"
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
            :status="room.status"
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
        <div class="history-info-table-column__info">{{ leader.nickName }}</div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.member_list') }}
        </div>
        <div class="history-info-table-column__info member-list">
          <profile-list
            :users="room.memberList"
            :customStyle="{
              width: '38px',
              height: '38px',
              'font-size': '14px',
              'margin-left': '12px',
            }"
            size="38px"
          ></profile-list>
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.active_date') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ room.activeDate | dateTimeFormat }}
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.unactive_date') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ room.unactiveDate | dateTimeFormat }}
        </div>
      </div>
      <div class="history-info__row">
        <div class="history-info-table-column__header">
          {{ $t('room.duration_sec') }}
        </div>
        <div class="history-info-table-column__info time">
          {{ room.durationSec | durationFormat }}
        </div>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileList from 'ProfileList'
import CollaboStatus from 'CollaboStatus'
import { getHistorySingleItem } from 'api/http/history'

import { dateTimeFormat, durationFormat } from 'utils/dateFormat'
export default {
  name: 'HistoryInfo',
  components: {
    Modal,
    ProfileList,
    CollaboStatus,
  },
  data() {
    return {
      room: {},
      visibleFlag: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    sessionId: {
      type: String,
      required: true,
    },
  },
  filters: {
    dateTimeFormat(dateTime) {
      return dateTimeFormat(dateTime)
    },
    durationFormat(time) {
      return durationFormat(time)
    },
  },
  computed: {
    leader() {
      return this.room.memberList
        ? this.room.memberList.find(member => {
            return member.memberType === 'LEADER'
          })
        : ''
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
      try {
        this.room = {}
        this.room = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
      } catch (err) {
        console.error(err)
      }
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },
}
</script>

<style lang="scss">
.modal.modal-history-info {
  .modal--header {
    height: 68px;
    padding: 22px 0px 16px 27px;
    color: rgb(11, 31, 72);
    font-weight: normal;

    font-size: 16px;
    background-color: #f8f8fa;
  }

  .modal--body {
    padding: 40px 45px 45px 45px;
  }

  .modal--close {
    width: 14px;
    height: 14px;
  }
}

.history-info--header {
  color: rgb(11, 31, 72);
  font-weight: normal;
  font-size: 22px;
}

.history-info-table {
  width: 720px;
  height: 518;
  margin-top: 16px;
  border-top: 2px solid rgb(234, 237, 243);
}

.history-info__row {
  display: flex;
  height: 74px;
  border-bottom: 1px solid #eaedf3;
}

.history-info-table-column__header {
  display: flex;
  align-items: center;
  width: 160px;
  padding-left: 20px;
  color: rgb(65, 74, 89);
  font-weight: normal;
  font-size: 15px;
  background-color: #f5f7fa;
}

.history-info-table-column__info {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  padding-left: 30px;
  color: #0b1f48;
  font-weight: 500;
  font-size: 16px;

  & > p {
    width: 400px;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &.time {
    color: rgb(15, 117, 245);
  }

  &.member-list {
    padding: 23px 0px 18px 18px;
  }
}

.collabo-status.custom-collabo-status {
  position: absolute;
  top: 24px;
  right: 0px;
}
</style>
