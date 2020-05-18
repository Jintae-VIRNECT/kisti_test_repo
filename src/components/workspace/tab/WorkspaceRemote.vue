<template>
  <tab-view
    title="진행중인 원격협업"
    description="진행중인 협업참여 요청 대기 목록을 보여줍니다."
    placeholder="협업, 멤버 검색"
    :emptyImage="require('assets/image/img_remote_empty.svg')"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :listCount="rooms.length"
    :empty="rooms.length === 0"
    :showRefreshButton="true"
    @refresh="refresh"
  >
    <div class="groupcard-list">
      <remote-card
        v-for="room of roomList"
        :key="room.roomId"
        :room="room"
        @join="joinRoom"
        @leave="leaveRoom"
        @remove="removeRoom"
      ></remote-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'

import { getRoomList, deleteRoom } from 'api/workspace/room'
import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'
export default {
  name: 'WorkspaceRemote',
  mixins: [searchMixin, confirmMixin],
  components: { TabView, RemoteCard },
  data() {
    return {
      remoteInfo: null,
      rooms: [],
    }
  },
  computed: {
    roomList() {
      return this.getFilter(this.rooms, ['title', 'description'], 'room')
    },
  },
  watch: {},
  methods: {
    async refresh() {
      this.remoteInfo = await getRoomList()
      this.rooms = this.remoteInfo.rooms
    },
    async remove(roomId) {
      const rtn = await deleteRoom({ roomId: roomId })

      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        if (rtn) {
          this.refresh()
          this.$eventBus.$emit('popover:close')
        }
      })
    },
    joinRoom(roomId) {
      console.log('참가하기::' + roomId)
      this.confirmDefault('이미 삭제된 협업입니다.')
      this.confirmDefault('협업에 참가가 불가능합니다.')
    },
    leaveRoom(roomId) {
      this.confirmCancel(
        '협업에서 나가시겠습니까?',
        {
          text: '나가기',
          action: () => {
            this.remove(roomId)
          },
        },
        { text: '취소' },
      )
    },
    removeRoom(roomId) {
      this.confirmCancel(
        '협업을 삭제 하시겠습니까?',
        {
          text: '확인',
          action: () => {
            this.remove(roomId)
          },
        },
        { text: '취소' },
      )
    },
  },

  /* Lifecycles */
  async created() {
    this.remoteInfo = await getRoomList({
      title: '이건 무슨 데이터일까',
      participantName: this.account.userId,
    })
    this.rooms = this.remoteInfo.rooms
  },
  mounted() {},
}
</script>
