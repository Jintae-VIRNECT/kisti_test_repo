<template>
  <div class="list-wrapper">
    <wide-card-extend
      :menu="true"
      v-for="(history, index) in list"
      v-bind:key="index"
    >
      <profile
        :image="history.path"
        :imageAlt="'profileImg'"
        :mainText="history.title"
        :subText="`참석자 ${history.participantsCount}명`"
        :thumbStyle="{ width: '3em', height: '3em' }"
        :group="true"
      ></profile>

      <div slot="column1" class="label label--noraml">
        {{ `총 이용시간: ${convertTime(history.totalUseTime)}` }}
      </div>
      <div slot="column2" class="label label--date">
        {{ convertDate(history.startDate) }}
      </div>
      <div slot="column3" class="label label__icon">
        <img class="icon" :src="require('assets/image/ic_leader.svg')" />
        <span class="text">{{ `리더 : ${history.leaderNickName}` }}</span>
      </div>
      <button slot="menuPopover"></button>
      <button
        class="btn small"
        @click="createRoom(history.roomId)"
        slot="column4"
      >
        재시작
      </button>
      <popover
        slot="column5"
        trigger="click"
        placement="bottom-start"
        popperClass="custom-popover"
      >
        <button slot="reference" class="widecard-tools__menu-button"></button>
        <ul class="groupcard-popover">
          <li>
            <button
              class="group-pop__button"
              @click="openRoomInfo(history.roomId)"
            >
              상세 보기
            </button>
          </li>
          <li>
            <button
              class="group-pop__button"
              @click="showDeleteDialog(history.roomId)"
            >
              목록삭제
            </button>
          </li>
        </ul>
      </popover>
    </wide-card-extend>
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
import Profile from 'Profile'
import WideCardExtend from 'WideCardExtend'

import sort from 'mixins/filter'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import Popover from 'Popover'
import RoominfoModal from '../../workspace/modal/WorkspaceRoomInfo'
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import { getPermission } from 'utils/deviceCheck'

import { deleteHistorySingleItem } from 'api/workspace/history'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceHistoryList',
  mixins: [sort, confirmMixin],
  components: {
    Profile,
    WideCardExtend,
    CreateRoomModal,
    Popover,
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
      if (this.searchFilter === '') {
        return this.historyList
      }

      const array = []
      for (const list of this.historyList) {
        if (list.title.toLowerCase().match(this.searchFilter.toLowerCase())) {
          array.push(list)
        }
      }
      return array
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
        console.log('convertDate :: Invalid data')
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
