<template>
  <tab-view
    :title="$t('workspace.remote_title')"
    :description="$t('workspace.remote_list_description')"
    :placeholder="$t('workspace.search_room')"
    :emptyImage="require('assets/image/img_remote_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :listCount="rooms.length"
    :empty="roomList.length === 0"
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
        @init="init"
      ></remote-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import RemoteCard from 'RemoteCard'
import { getRoomList, deleteRoom, leaveRoom } from 'api/http/room'
import confirmMixin from 'mixins/confirm'
import searchMixin from 'mixins/filter'
import roomMixin from 'mixins/room'

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
        'memberList[].nickname',
      ])
    },
    emptyTitle() {
      if (this.rooms.length > 0) {
        return this.$t('workspace.search_empty')
      } else {
        return this.$t('workspace.remote_empty')
      }
    },
    emptyDescription() {
      if (this.rooms.length > 0) {
        return ''
      } else {
        return this.$t('workspace.tab_empty_description')
      }
    },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid) {
        this.refresh()
      }
    },
    'list.length': 'scrollReset',
  },
  methods: {
    async refresh() {
      this.loading = true
      await this.init()
      this.loading = false
    },
    leave(sessionId) {
      this.confirmCancel(
        this.$t('workspace.confirm_remote_leave'),
        {
          text: this.$t('button.leave'),
          action: () => {
            this.leaveoutRoom(sessionId)
          },
        },
        { text: this.$t('button.cancel') },
      )
    },
    remove(sessionId) {
      this.confirmCancel(
        this.$t('workspace.confirm_remove_room'),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.removeRoom(sessionId)
          },
        },
        { text: this.$t('button.cancel') },
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
      try {
        const rtn = await deleteRoom({
          sessionId,
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })

        this.$eventBus.$emit('popover:close')
        this.confirmDefault(this.$t('workspace.confirm_removed_room'))
        this.$nextTick(() => {
          if (rtn) {
            this.refresh()
          }
        })
      } catch (err) {
        if (err.code === 4017) {
          this.toastError(this.$t('workspace.confirm_already_invite_remove'))
        }
        this.refresh()
      }
    },
    async leaveoutRoom(sessionId) {
      try {
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
      } catch (err) {
        if (err.code === 4015) {
          this.toastError(this.$t('workspace.confirm_remote_leader_leave'))
        } else if (err.code === 4017) {
          this.toastError(this.$t('workspace.confirm_already_invite_leave'))
        }
        this.refresh()
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
