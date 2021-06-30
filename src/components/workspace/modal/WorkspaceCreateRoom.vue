<template>
  <modal
    :title="$t('workspace.create_remote')"
    width="78.429em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="createroom-modal"
  >
    <div class="createroom">
      <create-room-info
        :key="'roominfo_' + sessionId"
        :roomInfo="roomInfo"
        :selection="selection"
        :btnLoading="clicked"
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
import { mapGetters, mapActions } from 'vuex'
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'

import { getHistorySingleItem } from 'api/http/history'
import {
  createRoom,
  restartRoom,
  updateRoomProfile,
  getRoomInfo,
} from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import { getMemberList } from 'api/http/member'
import { maxParticipants } from 'utils/callOptions'
import { ROOM_STATUS } from 'configs/status.config'
import callMixin from 'mixins/call'
import { isRegisted } from 'utils/auth'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [toastMixin, confirmMixin, callMixin],
  components: {
    Modal,
    CreateRoomInfo,
    CreateRoomInvite,
  },
  data() {
    return {
      selection: [],
      selectHistory: [],
      visibleFlag: false,
      users: [],
      maxSelect: maxParticipants - 1,
      roomInfo: {},
      loading: false,
      clicked: false,
    }
  },
  computed: {
    ...mapGetters(['targetCompany', 'restrictedMode', 'useScreenStrict']),
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
    async visible(flag) {
      if (flag) {
        this.selection = []
        this.selectHistory = []
        if (this.sessionId && this.sessionId.length > 0) {
          await this.getInfo()
        }
        this.inviteRefresh()
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
        this.selectHistory = this.roomInfo.memberList.filter(
          member => member.uuid !== this.account.uuid,
        )
      } catch (err) {
        console.error(err)
      }
    },
    reset() {
      this.selection = []
      this.selectHistory = []
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
      const inviteList = await getMemberList({
        size: 50,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      this.users = inviteList.memberList
      this.users.sort((A, B) => {
        if (A.role === 'MASTER') {
          return -1
        } else if (B.role === 'MASTER') {
          return 1
        } else if (A.role === 'MANAGER' && B.role !== 'MANAGER') {
          return -1
        } else {
          return 0
        }
      })
      this.selection = this.users.filter(
        user =>
          this.selectHistory.findIndex(history => history.uuid === user.uuid) >
          -1,
      )
      this.loading = false
    },
    async startRemote(info) {
      try {
        //멤버 상태 등록 안된 경우 협업방 입장 불가
        if (!isRegisted) {
          this.toastDefault(this.$t('workspace.auth_status_failed'))
          return
        }

        if (this.clicked === true) return
        this.clicked = true

        this.$eventBus.$emit('roomloading:show', true)

        const options = await this.getDeviceId()
        const mediaStream = await this.$call.getStream(options)

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

        let createdRes

        if (
          this.sessionId &&
          this.sessionId.length > 0 &&
          info.imageUrl &&
          info.imageUrl !== 'default'
        ) {
          createdRes = await restartRoom({
            client: 'DESKTOP',
            userId: this.account.uuid,
            title: info.title,
            description: info.description,
            leaderId: this.account.uuid,
            participantIds: selectedUserIds,
            workspaceId: this.workspace.uuid,
            sessionId: this.sessionId,
            sessionType: ROOM_STATUS.PRIVATE,
            companyCode: this.targetCompany,
            videoRestrictedMode:
              this.restrictedMode.video && this.useScreenStrict,
            audioRestrictedMode:
              this.restrictedMode.audio && this.useScreenStrict,
          })
        } else {
          createdRes = await createRoom({
            client: 'DESKTOP',
            userId: this.account.uuid,
            title: info.title,
            description: info.description,
            leaderId: this.account.uuid,
            participantIds: selectedUserIds,
            workspaceId: this.workspace.uuid,
            sessionType: ROOM_STATUS.PRIVATE,
            companyCode: this.targetCompany,
            videoRestrictedMode:
              this.restrictedMode.video && this.useScreenStrict,
            audioRestrictedMode:
              this.restrictedMode.audio && this.useScreenStrict,
          })
        }
        if (info.imageFile) {
          await updateRoomProfile({
            profile: info.imageFile,
            sessionId: createdRes.sessionId,
            uuid: this.account.uuid,
            workspaceId: this.workspace.uuid,
          })
        }

        const connRes = await this.$call.connect(
          createdRes,
          ROLE.LEADER,
          options,
          mediaStream,
        )

        const roomInfo = await getRoomInfo({
          sessionId: createdRes.sessionId,
          workspaceId: this.workspace.uuid,
        })

        this.setRoomInfo({
          ...roomInfo,
          leaderId: this.account.uuid,
          open: false,
          videoRestrictedMode: createdRes.videoRestrictedMode,
          audioRestrictedMode: createdRes.audioRestrictedMode,
        })
        if (connRes) {
          this.$eventBus.$emit('popover:close')

          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('join room fail')
          this.clicked = false
        }
      } catch (err) {
        this.clicked = false
        this.$eventBus.$emit('roomloading:show', false)
        this.roomClear()
        if (typeof err === 'string') {
          console.error(err)
          if (err === 'nodevice') {
            this.toastError(this.$t('workspace.error_no_connected_device'))
            return
          } else if (err.toLowerCase() === 'requested device not found') {
            this.toastError(this.$t('workspace.error_no_device'))
            return
          } else if (err.toLowerCase() === 'device access deined') {
            this.$eventBus.$emit('devicedenied:show')
            return
          }
        } else if (err.code === 7003) {
          this.toastError(this.$t('service.file_type'))
        } else if (err.code === 7004) {
          this.toastError(this.$t('service.file_maxsize'))
        } else if (err.code === 7017) {
          this.toastError(this.$t('alarm.file_storage_capacity_full'))
        } else {
          console.error(`${err.message} (${err.code})`)
          this.toastError(this.$t('confirm.network_error'))
        }
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
