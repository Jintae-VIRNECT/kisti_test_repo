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
import { getRoomList, deleteRoom, leaveRoom } from 'api/workspace'
import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'
import roomMixin from 'mixins/room'

import { mapActions } from 'vuex'
export default {
  name: 'WorkspaceRemote',
  mixins: [searchMixin, confirmMixin, roomMixin],
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

      if (rtn) {
        this.refresh()
        this.$nextTick(() => {
          this.$eventBus.$emit('popover:close')
        })
      }
    },
  },

  /* Lifecycles */
  created() {
    this.init()
  },
  mounted() {},
}
</script>
