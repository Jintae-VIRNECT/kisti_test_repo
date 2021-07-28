import { joinRoom } from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import { ROOM_STATUS } from 'configs/status.config'
import { ERROR } from 'configs/error.config'

import toastMixin from 'mixins/toast'
import callMixin from 'mixins/call'
import errorMsgMixin from 'mixins/errorMsg'
import confirmMixin from 'mixins/confirm'

import { isRegisted } from 'utils/auth'
import { mapActions } from 'vuex'

import { getRoomInfo } from 'api/http/room'

export default {
  mixins: [toastMixin, callMixin, errorMsgMixin, confirmMixin],
  data() {
    return {
      clicked: false,
    }
  },
  methods: {
    ...mapActions(['roomClear', 'setRoomInfo']),
    async join(room, showRestrictAgreeModal = true) {
      this.logger('>>> JOIN ROOM')
      try {
        //멤버 상태 등록 안된 경우 협업방 입장 불가
        if (!isRegisted) {
          this.toastDefault(this.$t('workspace.auth_status_failed'))
          return
        }

        if (this.clicked === true) return
        this.clicked = true

        let role
        if (room.sessionType === ROOM_STATUS.PRIVATE) {
          let myInfo = room.memberList.find(
            member => member.uuid === this.account.uuid,
          )
          if (myInfo === undefined) throw Error('not allow to participant')
          role = myInfo.memberType === ROLE.LEADER ? ROLE.LEADER : ROLE.EXPERT
        } else {
          role = room.leaderId === this.account.uuid ? ROLE.LEADER : ROLE.EXPERT
        }

        room.videoRestrictedMode = this.checkVideoStrictMode(room)

        const showVideoRestrictModal =
          role !== ROLE.LEADER &&
          showRestrictAgreeModal &&
          room.videoRestrictedMode

        if (showVideoRestrictModal) {
          this.connectCancel(
            this.$t('workspace.confirm_video_restrict_join'),
            {
              text: this.$t('button.connect'),
              action: () => {
                this.doJoin(room, role)
              },
            },
            {
              text: this.$t('button.cancel'),
              action: () => {
                this.clicked = false
              },
            },
          )
        } else {
          this.doJoin(room, role)
        }
      } catch (err) {
        this.clicked = false
        this.$eventBus.$emit('roomloading:show', false)
        this.roomClear()
        if (this['init'] && typeof this['init'] === 'function') {
          this.init()
        }
        if (typeof err === 'string') {
          console.error(err)
          if (err === 'nodevice') {
            this.toastError(this.$t('workspace.error_no_connected_device'))
            return false
          } else if (err.toLowerCase() === 'requested device not found') {
            this.toastError(this.$t('workspace.error_no_device'))
            return false
          } else if (err.toLowerCase() === 'device access deined') {
            this.$eventBus.$emit('devicedenied:show')
            return false
          }
        } else {
          console.error(`${err.message} (${err.code})`)
          if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
            this.showErrorToast(err.code)
            return false
          } else if (err.code === ERROR.REMOTE_ALREADY_INVITE) {
            this.showErrorToast(err.code)
            return false
          } else if (err.code === 4021) {
            this.toastError(
              this.$t('workspace.remote_access_overflow', {
                num: room.maxUserCount,
              }),
            )
            return false
          }
        }
        this.toastError(this.$t('workspace.remote_invite_impossible'))
      }
    },
    async doJoin(room, role) {
      this.$eventBus.$emit('roomloading:show', true)

      const options = await this.getDeviceId()
      const mediaStream = await this.$call.getStream(options)

      const res = await joinRoom({
        uuid: this.account.uuid,
        memberType: role,
        deviceType: DEVICE.WEB,
        sessionId: room.sessionId,
        workspaceId: this.workspace.uuid,
      })

      this.setRoomInfo({
        ...room,
        audioRestrictedMode: res.audioRestrictedMode,
        videoRestrictedMode: res.videoRestrictedMode,
      })

      const joinRtn = await this.$call.connect(res, role, options, mediaStream)
      if (joinRtn) {
        this.$nextTick(() => {
          this.$router.push({ name: 'service' })
        })
        return true
      } else {
        this.roomClear()
        console.error('>>>join room fail')
        this.clicked = false
      }
    },

    async checkVideoStrictMode(room) {
      if (!('videoRestrictedMode' in room)) {
        const params = {
          workspaceId: this.workspace.uuid,
          sessionId: room.sessionId,
        }
        const roomInfo = await getRoomInfo(params)
        return roomInfo.videoRestrictedMode
      } else {
        return false
      }
    },
  },
}
