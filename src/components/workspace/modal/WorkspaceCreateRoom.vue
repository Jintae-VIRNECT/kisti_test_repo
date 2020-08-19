<template>
  <modal
    :title="$t('workspace.create_remote')"
    width="78.429em"
    height="54.286em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="createroom-modal"
  >
    <div class="createroom">
      <create-room-info
        :roomInfo="roomInfo"
        :selection="selection"
        :nouser="users.length === 0"
        @startRemote="startRemote"
      ></create-room-info>
      <create-room-invite
        :users="users"
        :selection="selection"
        @userSelect="selectUser"
        @inviteRefresh="inviteRefresh"
        :loading="loading"
      ></create-room-invite>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { mapActions } from 'vuex'
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'

import { createRoom } from 'api/workspace/room'
import { sendPush } from 'api/common/message'
import { ROLE } from 'configs/remote.config'
import { getHistorySingleItem } from 'api/workspace/history'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import { EVENT } from 'configs/push.config'
import { getMember } from 'api/service'

import { getPermission } from 'utils/deviceCheck'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [toastMixin, confirmMixin],
  components: {
    Modal,
    CreateRoomInfo,
    CreateRoomInvite,
  },
  data() {
    return {
      selection: [],
      visibleFlag: false,
      users: [],
      maxSelect: 2,
      roomInfo: {},
      loading: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    sessionId: {
      type: String,
      default: '',
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.selection = []
        this.inviteRefresh()
        if (this.sessionId && this.sessionId.length > 0) {
          this.getInfo()
        }
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear', 'updateAccount']),
    async getInfo() {
      try {
        this.roomInfo = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
        })
        for (let member of this.roomInfo.memberList) {
          if (member.uuid !== this.account.uuid) {
            this.selection.push(member)
          }
        }
      } catch (err) {
        console.error(err)
      }
    },
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify(this.$t('workspace.create_max_member'))
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async inviteRefresh() {
      this.loading = true
      const inviteList = await getMember({
        size: 100,
        workspaceId: this.workspace.uuid,
      })
      this.users = inviteList.memberInfoList
        .filter(member => member.uuid !== this.account.uuid)
        .sort((a, b) => {
          var nameA = a.name.toUpperCase()
          var nameB = b.name.toUpperCase()
          if (nameA < nameB) {
            return -1
          }
          if (nameA > nameB) {
            return 1
          }

          // 이름이 같을 경우
          return 0
        })
      this.loading = false
    },
    async startRemote(info) {
      const permission = await getPermission()

      if (!permission) {
        this.$eventBus.$emit('devicedenied:show')
        return
      }

      try {
        const selectedUser = []
        const selectedUserIds = []

        for (let select of this.selection) {
          selectedUser.push({
            id: select.uuid,
            uuid: select.uuid,
            email: select.email,
          })
          selectedUserIds.push(select.uuid)
        }

        const createdRes = await createRoom({
          file: info.imageFile,
          title: info.title,
          description: info.description,
          leaderId: this.account.uuid,
          leaderEmail: this.account.email,
          participants: selectedUser,
          workspaceId: this.workspace.uuid,
        })
        const connRes = await this.$call.connect(createdRes, ROLE.LEADER)

        const roomInfo = {
          sessionId: createdRes.sessionId,
          title: info.title,
          description: info.description,
          leaderId: this.account.uuid,
          participantsCount: selectedUser.length + 1,
          maxParticipantCount: 3,
          memberList: [...selectedUser, this.account],
          token: createdRes.token,
          coturn: createdRes.coturn,
          wss: createdRes.wss,
        }

        this.setRoomInfo(roomInfo)
        if (connRes) {
          this.$eventBus.$emit('popover:close')

          const contents = {
            roomSessionId: createdRes.sessionId,
            title: info.title,
            nickName: this.account.nickname,
            profile: this.account.profile,
          }

          await sendPush(EVENT.INVITE, selectedUserIds, contents)

          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('join room fail')
        }
      } catch (err) {
        this.roomClear()
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
