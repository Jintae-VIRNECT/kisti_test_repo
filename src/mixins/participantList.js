import { mapGetters, mapActions } from 'vuex'
import { SIGNAL, VIDEO, ROLE } from 'configs/remote.config'
import { kickoutMember } from 'api/http/member'
import { maxParticipants } from 'utils/callOptions'

export default {
  computed: {
    ...mapGetters([
      'participants',
      'mainView',
      'viewForce',
      'roomInfo',
      'openRoom',
      'restrictedRoom',
      'isMobileSize',
    ]),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isGuest() {
      return this.account.roleType === ROLE.GUEST
    },
    isMaxLength() {
      return this.participants.length === maxParticipants
    },
  },
  data() {
    return {
      selectview: false,
      invite: false,
      force: null,
    }
  },
  methods: {
    ...mapActions(['setMainView', 'addChat', 'removeMember']),

    //제한모드에서 영상 전체 공유 혹은 참가자 영상 클릭 메뉴 열기
    selectMain(participant) {
      if (this.restrictedRoom && !this.isMobileSize) {
        this.$call.sendVideo(participant.id, true)
        return
      }
      this.selectview = {
        id: participant.id,
        nickname: participant.nickname,
        mute: participant.mute,
        connectionId: participant.connectionId,
      }
    },

    //음소거
    mute(participant) {
      this.$call.mute(participant.connectionId, !participant.mute)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
      })
    },

    //게스트 초대 모달 열기
    showGuestInviteModal() {
      this.$eventBus.$emit('guestInvite:show', true)
    },

    //내보내기
    async kickout(participantId) {
      const params = {
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        leaderId: this.account.uuid,
        participantId,
      }
      const rtn = await kickoutMember(params)
      if (rtn.result === true) {
        this.removeMember(participantId)
      }
      // this.$call.disconnect(this.participant.connectionId)
    },

    //선택한 참가자 영상 보기
    normal() {
      this.changeMainView(this.selectview, false)
    },

    //선택한 참가자 화면을 전체 공유하기
    share() {
      this.changeMainView(this.selectview, true)
    },

    //원격협업 메인 영상 변경 (영상 보기 or 전체 공유)
    changeMainView(select, force) {
      this.selectview = false
      if (
        this.account.roleType === ROLE.LEADER &&
        select.id === this.mainView.id &&
        this.viewForce === force
      ) {
        return
      }

      this.$call.sendVideo(select.id, force)
      this.setMainView({ id: select.id, force })
    },

    // video 제어 시그널 수신 이벤트 헨들러
    signalVideo(event) {
      const data = JSON.parse(event.data)
      if (data.type === VIDEO.SHARE) {
        const participant = this.participants.find(user => user.id === data.id)
        this.addChat({
          name: participant.nickname,
          status: this.isLeader ? 'sharing-start-leader' : 'sharing-start',
          type: 'system',
        })
        this.force = data.id
      } else if (data.type === VIDEO.NORMAL) {
        if (this.force) {
          const participant = this.participants.find(
            user => user.id === this.force,
          )
          if (participant) {
            this.addChat({
              name: participant.nickname,
              status: this.isLeader ? 'sharing-stop-leader' : 'sharing-stop',
              type: 'system',
            })
          }
        }
        this.force = null
      }
    },
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.VIDEO, this.signalVideo)
  },
  created() {
    this.$eventBus.$on(SIGNAL.VIDEO, this.signalVideo)
  },
}
