<template>
  <tab-view
    :title="$t('workspace.remote_title')"
    :description="$t('workspace.remote_list_description')"
    :placeholder="$t('workspace.search_room')"
    :emptyImage="require('assets/image/img_remote_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :listCount="roomList.length"
    :empty="roomList.length === 0"
    :showRefreshButton="true"
    :loading="loading"
    @refresh="refresh"
    @search="doSearch"
  >
    <div class="groupcard-list">
      <remote-card
        v-for="room of roomList"
        :key="room.sessionId"
        :room="room"
        @join="roomInfo => join(roomInfo)"
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
import {
  getRoomList,
  searchRoomList,
  deleteRoom,
  leaveRoom,
} from 'api/http/room'

import { ERROR } from 'configs/error.config'

import confirmMixin from 'mixins/confirm'
import roomMixin from 'mixins/room'
import errorMsgMixin from 'mixins/errorMsg'

export default {
  name: 'WorkspaceRemote',
  mixins: [confirmMixin, roomMixin, errorMsgMixin],
  components: { TabView, RemoteCard },
  data() {
    return {
      rooms: [],
      loading: false,
      searchRooms: [],
      searchText: '',
    }
  },
  computed: {
    roomList() {
      if (this.searchText.length > 0) {
        return this.searchRooms
      } else {
        return this.rooms
      }
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
    async doSearch(text) {
      if (this.rooms.length === 0) return
      try {
        if (!text || text.trim().length === 0) {
          this.searchText = ''
          this.searchRooms = []
          return
        }
        const roomList = await searchRoomList({
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
          search: text,
        })
        this.searchText = text
        this.searchRooms = roomList.roomInfoList
      } catch (err) {
        this.searchText = ''
        this.searchRooms = []
      }
    },
    async init() {
      this.searchText = ''
      this.searchRooms = []
      this.$eventBus.$emit('search:clear')
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
        if (err.code === ERROR.CONFIRM_REMOTE_LEADER_LEAVE) {
          this.showErrorToast(err.code)
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
