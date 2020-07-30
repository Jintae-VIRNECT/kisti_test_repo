import { joinRoom } from 'api/workspace'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import { getPermission } from 'utils/deviceCheck'
export default {
  methods: {
    async join(room) {
      const permission = await getPermission()

      if (!permission) {
        this.$eventBus.$emit('devicedenied:show')
        return false
      }

      this.logger('>>> JOIN ROOM')
      try {
        this.setRoomInfo(room)
        let myInfo = room.memberList.find(
          member => member.uuid === this.account.uuid,
        )
        let role =
          myInfo.memberType === ROLE.EXPERT_LEADER
            ? ROLE.EXPERT_LEADER
            : ROLE.EXPERT

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
          console.error('>>>join room 실패')
        }
      } catch (err) {
        if (err.code === 4002) {
          this.toastError('이미 삭제된 협업입니다.')
        }
        this.roomClear()
      }
      // this.confirmDefault('이미 삭제된 협업입니다.')
      // this.confirmDefault('협업에 참가가 불가능합니다.')
    },
  },
}
