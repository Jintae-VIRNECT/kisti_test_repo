<template>
  <div v-if="historyList.length > 0" class="list-wrapper">
    <wide-card-extend
      :menu="true"
      v-for="(history, index) in list"
      v-bind:key="index"
    >
      <profile
        :image="require('assets/image/img_default_group.svg')"
        :imageAlt="'profileImg'"
        :mainText="history.title"
        :subText="'참석자 ' + history.memberCount + '명'"
      ></profile>

      <div slot="column1" class="label label--noraml">
        {{ '총 이용시간: ' + history.totalUseTime }}
      </div>
      <div slot="column2" class="label label--date">
        {{ convertDate(history.collaborationStartDate) }}
      </div>
      <div slot="column3" class="label label__icon">
        <img class="icon" :src="require('assets/image/ic_leader.svg')" />
        <span class="text">{{ '리더 : ' + history.roomLeaderName }}</span>
      </div>
      <button slot="menuPopover"></button>
      <button class="btn" @click="createRoom(history.roomId)" slot="column4">
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
              @click="deleteItem(history.roomId)"
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
    <create-room-modal :visible.sync="visible"></create-room-modal>
  </div>
  <div v-else class="no-list">
    <div class="no-list__img"></div>
    <div class="no-list__title">협업 가능 멤버가 없습니다.</div>
    <div class="no-list__description">협업 멤버를 추가해주세요.</div>
  </div>
</template>

<script>
import Profile from 'Profile'
import WideCardExtend from 'WideCardExtend'

import sort from 'mixins/filter'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import Popover from 'Popover'
import RoominfoModal from '../../workspace/modal/WorkspaceRoomInfo'
import {
  getHistorySingleItem,
  deleteHistorySingleItem,
} from 'api/workspace/history'

export default {
  name: 'WorkspaceHistoryList',
  mixins: [sort],
  components: {
    Profile,
    WideCardExtend,
    CreateRoomModal,
    Popover,
    RoominfoModal,
  },
  data() {
    return {
      visible: false,
      showRoomInfo: false,
      roomId: '',
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
    async openRoomInfo(roomId) {
      let result = await getHistorySingleItem({ roomId })
      this.roomInfo = result.data
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        this.showRoomInfo = !this.showRoomInfo
      })
    },
    async deleteItem(roomId) {
      const pos = this.historyList.findIndex(room => {
        return room.roomId === roomId
      })
      this.historyList.splice(pos, 1)
      await deleteHistorySingleItem({ roomId })
    },

    //재시작
    async createRoom(roomId) {
      this.roomId = roomId
      this.visible = !this.visible
    },
    convertDate(date) {
      const re = /-/gi
      return date.replace(re, '.')
    },
  },
  created() {},
}
</script>
