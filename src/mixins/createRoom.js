import {
  createRoom,
  restartRoom,
  updateRoomProfile,
  getRoomInfo,
} from 'api/http/room'
import { getMemberList } from 'api/http/member'
import { getHistorySingleItem } from 'api/http/history'

import { isRegisted } from 'utils/auth'
import { memberSort } from 'utils/sort'
import { ROOM_STATUS } from 'configs/status.config'
import { ROLE } from 'configs/remote.config'
import toastMixin from 'mixins/toast'
import callMixin from 'mixins/call'
import { mapActions } from 'vuex'

export default {
  mixins: [toastMixin, callMixin],
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
      this.callAndGetMobileResponsiveFunction(
        this.setVisibleMobileFlag,
        this.setVisiblePcFlag,
      )
    },
  },
  methods: {
    setVisiblePcFlag() {
      this.visiblePcFlag = this.visible
      this.visibleMobileFlag = false
    },
    setVisibleMobileFlag() {
      this.visiblePcFlag = false
      this.visibleMobileFlag = this.visible
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    ...mapActions(['setRoomInfo', 'roomClear']),
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
    async inviteRefresh() {
      this.loading = true
      const inviteList = await getMemberList({
        size: 50,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      this.users = inviteList.memberList
      this.users.sort(memberSort)
      this.selection = this.users.filter(
        user =>
          this.selectHistory.findIndex(history => history.uuid === user.uuid) >
          -1,
      )
      this.loading = false
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

    //원격 협업 시작
    //info : {title, description, imageFile, imageUrl, open}
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

        const options = await this.getDeviceId() //callMixin
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

        //sessionId : props
        if (
          this.sessionId &&
          this.sessionId.length > 0 &&
          info.imageUrl &&
          info.imageUrl !== 'default'
        ) {
          //api
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
          //api
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
          //api
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

        //api
        const roomInfo = await getRoomInfo({
          sessionId: createdRes.sessionId,
          workspaceId: this.workspace.uuid,
        })

        //vuex action
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
          this.roomClear() //vuex action
          console.error('join room fail')
          this.clicked = false
        }
      } catch (err) {
        this.clicked = false
        this.$eventBus.$emit('roomloading:show', false)
        this.roomClear() //vuex action
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
  mounted() {
    this.responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.setVisibleMobileFlag,
      this.setVisiblePcFlag,
    )
    this.addEventListenerScreenResize(this.responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this.responsiveFn)
  },
}
