<template>
  <tab-view
    title="진행중인 원격협업"
    description="진행중인 협업참여 요청 대기 목록을 보여줍니다."
    placeholder="협업, 멤버 이름 검색"
    :emptyImage="require('assets/image/img_remote_empty.svg')"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :listCount="rooms.length"
    :empty="rooms.length === 0"
    :showRefreshButton="true"
    :loading="loading"
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

import { getRoomList, getRoomInfo, deleteRoom } from 'api/workspace/room'
import { mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'
import { ROLE } from 'configs/remote.config'
export default {
  name: 'WorkspaceRemote',
  mixins: [searchMixin, confirmMixin],
  components: { TabView, RemoteCard },
  data() {
    return {
      remoteInfo: null,
      rooms: [],
      loading: false,
    }
  },
  computed: {
    roomList() {
      return this.getFilter(this.rooms, ['title', 'description'])
    },
  },
  watch: {},
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear']),
    async refresh() {
      this.loading = true
      this.remoteInfo = await getRoomList()
      this.loading = false
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
    async joinRoom(room) {
      console.log('>>> JOIN ROOM')
      try {
        const roomInfo = await getRoomInfo({
          roomId: room.roomId,
        })

        this.setRoomInfo(roomInfo)
        let role = ''
        if (roomInfo.leaderId === this.account.uuid) {
          role = ROLE.EXPERT_LEADER
        } else {
          role = ROLE.EXPERT
        }

        const joinRtn = await this.$call.join(room, role)
        if (joinRtn) {
          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('>>>join room 실패')
        }
      } catch (err) {
        this.roomClear()
        console.log(err)
      }
      // this.confirmDefault('이미 삭제된 협업입니다.')
      // this.confirmDefault('협업에 참가가 불가능합니다.')
    },
    leaveRoom(roomId) {
      this.confirmCancel(
        '협업에서 나가시겠습니까?',
        {
          text: '나가기',
          action: () => {
            this.remove(roomId, this.account.nickname)
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
    async init() {
      this.remoteInfo = await getRoomList({
        title: '이건 무슨 데이터일까',
        participantName: this.account.userId,
      })
      this.rooms = this.remoteInfo.rooms
    },
  },

  /* Lifecycles */
  created() {
    this.init()
  },
  mounted() {},
}
</script>
