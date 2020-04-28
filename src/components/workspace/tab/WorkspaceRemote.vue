<template>
  <tab-view
    title="진행중인 원격협업"
    description="진행중인 협업참여 요청 대기 목록을 보여줍니다."
    placeholder="원격 협업 검색"
    :emptyImage="require('assets/image/img_remote_empty.svg')"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :listCount="currentCount"
    :empty="rooms.length === 0"
    :showRefreshButton="true"
    @refresh="refresh"
  >
    <div class="groupcard-list">
      <remote-card
        v-for="room of roomList"
        :key="room.roomId"
        :roomInfo="room"
        @refresh="refresh"
      ></remote-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'
import { getRoomList } from 'api/remote/room'
import searchMixin from 'mixins/filter'
export default {
  name: 'WorkspaceRemote',
  mixins: [searchMixin],
  components: { TabView, RemoteCard },
  data() {
    return {
      remoteInfo: null,
      rooms: [],
    }
  },
  computed: {
    // rooms() {
    //   if (!this.remoteInfo || !this.remoteInfo.rooms) {
    //     return []
    //   } else {
    //     return this.remoteInfo.rooms
    //   }
    // },
    currentCount() {
      if (!this.remoteInfo || !this.remoteInfo.currentCount) {
        return 0
      } else {
        return this.remoteInfo.currentCount
      }
    },
    roomList() {
      return this.getFilter(this.rooms, ['title', 'description'], 'room')
    },
  },
  watch: {},
  methods: {
    async refresh() {
      this.remoteInfo = await getRoomList({
        title: '이건 무슨 데이터일까',
        participantName: this.account.userId,
      })
      this.rooms = this.remoteInfo.rooms
      this.rooms = []
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
