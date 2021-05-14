import { joinRoom } from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import { ROOM_STATUS } from 'configs/status.config'
import toastMixin from 'mixins/toast'
import { mapActions } from 'vuex'
import callMixin from 'mixins/call'
export default {
  mixins: [toastMixin, callMixin],
  data() {
    return {
      clicked: false,
    }
  },
  methods: {
    ...mapActions(['roomClear', 'setRoomInfo']),
    async join(room) {
      this.logger('>>> JOIN ROOM')
      try {
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

        const joinRtn = await this.$call.connect(
          res,
          role,
          options,
          mediaStream,
        )
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
      } catch (err) {
        this.clicked = false
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
          if (err.code === 4002) {
            this.toastError(this.$t('workspace.remote_already_removed'))
            return false
          } else if (err.code === 4016) {
            this.toastError(this.$t('workspace.remote_already_invite'))
            return false
          }
        }
        this.toastError(this.$t('workspace.remote_invite_impossible'))
      }
    },
  },
}
