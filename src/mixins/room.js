import { joinRoom } from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import toastMixin from 'mixins/toast'
import { mapActions } from 'vuex'
export default {
  mixins: [toastMixin],
  methods: {
    ...mapActions(['roomClear', 'setRoomInfo']),
    async join(room) {
      this.logger('>>> JOIN ROOM')
      try {
        this.setRoomInfo(room)
        let myInfo = room.memberList.find(
          member => member.uuid === this.account.uuid,
        )
        let role = myInfo.memberType === ROLE.LEADER ? ROLE.LEADER : ROLE.EXPERT

        const res = await joinRoom({
          uuid: this.account.uuid,
          memberType: role,
          deviceType: DEVICE.WEB,
          sessionId: room.sessionId,
          workspaceId: this.workspace.uuid,
        })

        window.urls['token'] = res.token
        window.urls['coturn'] = res.coturn
        window.urls['wss'] = res.wss

        const joinRtn = await this.$call.connect(res, role)
        if (joinRtn) {
          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('>>>join room fail')
        }
      } catch (err) {
        if (typeof err === 'string') {
          if (err === 'nodevice') {
            this.toastError(this.$t('workspace.error_no_connected_device'))
          } else if (err.toLowerCase() === 'requested device not found') {
            this.toastError(this.$t('workspace.error_no_device'))
          } else if (err.toLowerCase() === 'device access deined') {
            this.$eventBus.$emit('devicedenied:show')
          }
        } else if (err.code === 4002) {
          this.toastError(this.$t('workspace.remote_already_removed'))
        } else if (err.code === 4016) {
          // TODO: MESSAGE
          this.toastError(this.$t('workspace.remote_already_invite'))
        } else {
          this.toastError(this.$t('workspace.remote_invite_impossible'))
        }
        this.roomClear()
        console.error(err)
        if (this['init'] && typeof this['init'] === 'function') {
          this.init()
        }
      }
    },
  },
}
