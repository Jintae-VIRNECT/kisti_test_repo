<template>
  <tab-view
    title="진행중인 원격협업"
    description="진행중인 협업참여 요청 대기 목록을 보여줍니다."
    placeholder="원격 협업 검색"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :listCount="currentCount"
    :empty="rooms.length === 0"
    customClass="remote"
    :showRefreshButton="true"
    @refresh="refresh"
  >
    <remote-card
      v-for="room of rooms"
      :key="room.roomId"
      :roomInfo="room"
      @refresh="refresh"
    ></remote-card>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'
import { getRoomList } from 'api/remote/room'
export default {
  name: 'WorkspaceRemote',
  components: { TabView, RemoteCard },
  data() {
    return {
      roomList: null,
    }
  },
  computed: {
    rooms() {
      if (!this.roomList || !this.roomList.rooms) {
        return []
      } else {
        return this.roomList.rooms
      }
    },
    currentCount() {
      if (!this.roomList || !this.roomList.currentCount) {
        return 0
      } else {
        return this.roomList.currentCount
      }
    },
  },
  watch: {
    rooms(val) {
      console.log(val)
    },
  },
  methods: {
    async refresh() {
      this.roomList = await getRoomList({
        title: '이건 무슨 데이터일까',
        participantName: this.account.userId,
      })
      console.log(this.roomList)
    },
  },

  /* Lifecycles */
  async created() {
    this.roomList = await getRoomList({
      title: '이건 무슨 데이터일까',
      participantName: this.account.userId,
    })
    console.log(this.roomList)
    console.log(this.roomList.currentCount)
  },
  mounted() {},
}
</script>
