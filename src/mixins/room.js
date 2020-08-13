import { joinRoom } from 'api/workspace'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import toastMixin from 'mixins/toast'
import { mapGetters } from 'vuex'
export default {
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['roomClear']),
  },
  methods: {
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
          email: this.account.email,
          memberType: role,
          deviceType: DEVICE.WEB,
          sessionId: room.sessionId,
          workspaceId: this.workspace.uuid,
        })

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
        if (err === 'nodevice') {
          this.toastError('연결된 디바이스를 찾을 수 없습니다.')
        } else if (err.code === 4002) {
          this.toastError(this.$t('workspace.remote_already_removed'))
        } else if (err.code === 4016) {
          // TODO: MESSAGE
          this.toastError(this.$t('workspace.remote_already_invite'))
        } else if (err.toLowerCase() === 'requested device not found') {
          this.toastError('디바이스를 찾을 수 없습니다.')
        } else if (err.toLowerCase() === 'device access deined') {
          this.$eventBus.$emit('devicedenied:show')
        } else {
          this.toastError(this.$t('workspace.remote_invite_impossible'))
        }
        this.roomClear()
      }
    },
  },
}
