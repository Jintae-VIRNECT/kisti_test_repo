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
        :key="room.sessionId"
        :room="room"
        @join="join(room)"
        @leave="leave(room.sessionId)"
        @remove="remove(room.sessionId)"
      ></remote-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'
import { getRoomList, deleteRoom, leaveRoom, joinRoom } from 'api/workspace'
import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'
import { DEVICE } from 'configs/device.config'
import { ROLE } from 'configs/remote.config'

import { mapActions } from 'vuex'
export default {
  name: 'WorkspaceRemote',
  mixins: [searchMixin, confirmMixin],
  components: { TabView, RemoteCard },
  data() {
    return {
      rooms: [],
      loading: false,
    }
  },
  computed: {
    roomList() {
      return this.getFilter(this.rooms, [
        'title',
        'description',
        'participants[].nickname',
      ])
    },
  },
  watch: {},
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear']),
    async refresh() {
      this.loading = true
      await this.init()
      this.loading = false
    },
    async join(room) {
      this.logger('>>> JOIN ROOM')
      try {
        this.setRoomInfo(room)
        let myInfo = room.memberList.find(
          member => member.uuid === this.account.uuid,
        )
        let role =
          myInfo.memberType === ROLE.EXPERT_LEADER
            ? ROLE.EXPERT_LEADER
            : ROLE.EXPERT

        const res = await joinRoom({
          uuid: this.account.uuid,
          email: this.account.email,
          memberType: role,
          deviceType: DEVICE.WEB,
          sessionId: room.sessionId,
          workspaceId: this.workspace.uuid,
        })

        const joinRtn = await this.$call.connect(res.token, role)
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
    leave(sessionId) {
      this.confirmCancel(
        '협업에서 나가시겠습니까?',
        {
          text: '나가기',
          action: () => {
            this.leaveoutRoom(sessionId)
          },
        },
        { text: '취소' },
      )
    },
    remove(sessionId) {
      this.confirmCancel(
        '협업을 삭제 하시겠습니까?',
        {
          text: '확인',
          action: () => {
            this.removeRoom(sessionId)
          },
        },
        { text: '취소' },
      )
    },
    async init() {
      const roomList = await getRoomList({
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })
      this.rooms = roomList.roomInfoList
    },
    async removeRoom(sessionId) {
      const rtn = await deleteRoom({
        sessionId,
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        if (rtn) {
          this.refresh()
        }
      })
    },
    async leaveoutRoom(sessionId) {
      const rtn = await leaveRoom({
        sessionId,
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        if (rtn) {
          this.refresh()
        }
      })
    },
  },

  /* Lifecycles */
  created() {
    this.init()
  },
  mounted() {},
}
</script>
