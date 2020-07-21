import { getRoomInfo } from 'api/workspace/room'
import { ROLE } from 'configs/remote.config'

export default {
  methods: {
    async joinRoom(roomId) {
      console.log('>>> JOIN ROOM')
      try {
        const roomInfo = await getRoomInfo({
          roomId: roomId,
        })

        this.setRoomInfo(roomInfo)
        let role = ''
        if (roomInfo.leaderId === this.account.uuid) {
          role = ROLE.EXPERT_LEADER
        } else {
          role = ROLE.EXPERT
        }

        const joinRtn = await this.$call.join(roomInfo, role)
        if (joinRtn) {
          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('>>>join room 실패')
        }
      } catch (err) {
        this.roomClear()
        console.log(err)
      }
      // this.confirmDefault('이미 삭제된 협업입니다.')
      // this.confirmDefault('협업에 참가가 불가능합니다.')
    },
  },
}
