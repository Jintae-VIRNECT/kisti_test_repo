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
  name: 'ModalHistoryInfo',
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
    height: 4.8571rem;
    padding: 1.5714rem 0rem 1.1429rem 1.9286rem;
    color: #0b1f48;
    font-weight: normal;

    font-size: 1.1429rem;
    background-color: #f8f8fa;
  }

  .modal--body {
    padding: 2.8571rem 3.2143rem 3.2143rem 3.2143rem;
  }

  .modal--close {
    width: 1rem;
    height: 1rem;
  }
}

.history-info--header {
  color: #0b1f48;
  font-weight: normal;
  font-size: 1.5714rem;
}

.history-info-table {
  width: 51.4286rem;
  height: 37rem;
  margin-top: 1.1429rem;
  border-top: 2px solid #eaedf3;
}

.history-info__row {
  display: flex;
  height: 5.2857rem;
  border-bottom: 1px solid #eaedf3;
}

.history-info-table-column__header {
  display: flex;
  align-items: center;
  width: 11.4286rem;
  padding-left: 1.4286rem;
  color: #414a59;
  font-weight: normal;
  font-size: 1.0714rem;
  background-color: #f5f7fa;
}

.history-info-table-column__info {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  padding-left: 2.1429rem;
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.1429rem;

  & > p {
    width: 28.5714rem;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &.time {
    color: #0f75f5;
  }

  &.member-list {
    padding: 1.6429rem 0rem 1.2857rem 1.2857rem;
  }
}

.collabo-status.custom-collabo-status {
  position: absolute;
  top: 1.7143rem;
  right: 0px;
}
</style>
