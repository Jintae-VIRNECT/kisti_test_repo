import { FLASH as FLASH_STATUS } from 'configs/device.config'
import toastMixin from 'mixins/toast'
import { mapGetters, mapMutations } from 'vuex'
import { ROLE, SIGNAL, FLASH } from 'configs/remote.config'

export default {
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['mainView', 'viewForce', 'myInfo', 'participants']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    flashStatus() {
      //전체 공유, 리더인 상황
      if (this.viewForce && this.isLeader) {
        //상대방의 영상을 전체공유하는 경우 : 상대방의 플래시 상태에 따라 결정
        if (this.mainView && this.mainView.id !== this.myInfo.id)
          return this.mainView.flash
        //자신의 영상을 전체공유하는 경우 : 자신의 플래시 상태에 따라 결정
        else if (this.mainView && this.mainView.id === this.myInfo.id)
          return this.myInfo.flash
        else return -1
      }
      //전체 공유아닌 상황(영상보기만 바꾸는 경우)에는 자신의 플래시 제어
      else {
        return this.myInfo.flash
      }
    },
    isFlashOn() {
      return this.flashStatus === FLASH_STATUS.FLASH_ON
    },
    isFlashDisable() {
      return (
        this.flashStatus === FLASH_STATUS.FLASH_NONE ||
        this.flashStatus === FLASH_STATUS.NO_PERMISSION ||
        this.flashStatus === 'default'
      )
    },
  },
  watch: {
    flashStatus(status, bStatus) {
      if (status !== -1) {
        this.flashListener(status, bStatus)
      }
    },
  },
  methods: {
    ...mapMutations(['updateMyInfo']),
    clickHandler() {
      if (this.flashStatus === FLASH_STATUS.FLASH_NONE) {
        this.toastDefault(this.$t('service.flash_none'))
        return
      }

      if (this.flashStatus === FLASH_STATUS.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
        return
      }
      const toStatus = !this.isFlashOn

      //전체 공유, 리더권한 인 경우
      if (this.viewForce && this.isLeader) {
        //자신의 플래시 제어
        if (this.mainView.id === this.myInfo.id) {
          //자신의 플래시 제어
          //시그널 전송
          this.$call.sendFlash(toStatus, [this.myInfo.connectionId])
        } else {
          this.$call.sendFlash(toStatus, [this.mainView.connectionId])
        }
      }
      //자신의 플래시 제어
      else {
        //자신의 플래시 제어
        //시그널 전송
        this.$call.sendFlash(toStatus, [this.myInfo.connectionId])
      }
    },
    flashListener(status, bStatus) {
      //내 플래시를 제어하는 경우거나 전체 공유가 아닌 상태에는 상대방 제어에 대한 토스트를 띄우지 않는다.
      if (this.mainView.id === this.myInfo.id || !this.viewForce) return

      // 응답
      if (parseInt(status) === FLASH_STATUS.CAMERA_ZOOMING) {
        this.toastNotice(this.$t('service.flash_controlling'))
        return
      }
      if (parseInt(status) === FLASH_STATUS.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
      }
      // if (parseInt(status) === FLASH_STATUS.FLASH_NONE) {
      //   this.toastDefault(this.$t('service.flash_none'))
      // }

      if (
        parseInt(status) === FLASH_STATUS.FLASH_ON &&
        parseInt(bStatus) === FLASH_STATUS.FLASH_OFF
      ) {
        this.toastDefault(this.$t('service.flash_on'))
      }
      if (
        parseInt(status) === FLASH_STATUS.FLASH_OFF &&
        parseInt(bStatus) === FLASH_STATUS.FLASH_ON
      ) {
        this.toastDefault(this.$t('service.flash_off'))
      }
      if (parseInt(status) === FLASH_STATUS.APP_IS_BACKGROUND) {
        this.toastDefault(this.$t('service.flash_app_disable'))
      }
    },

    //상대방이 내 플래시 제어할 때, 자신 기기 플래시 제어할 때 모두 사용되는 시그널 이벤트 리스너
    flashControlListener(event) {
      const data = JSON.parse(event.data)
      if (data.type !== FLASH.FLASH) return

      let stream = this.mainView.stream

      //내 기기 제어인 경우 stream을 내것으로 잡는다. (다른 참가자의 영상을 보고 있거나, 리더가 전체 공유자의 영상을 보는 경우에도 내 플래시 제어를 해야하므로)
      if (event.from.connectionId === this.myInfo.connectionId)
        stream = this.myInfo.stream

      //플래시 토글
      stream.getVideoTracks()[0].applyConstraints({
        advanced: [{ torch: data.enable }],
      })

      const flashStatus = data.enable
        ? FLASH_STATUS.FLASH_ON
        : FLASH_STATUS.FLASH_OFF

      //현재 플래시 상태를 업데이트 한다. (myInfo)
      this.updateMyInfo({
        flash: flashStatus,
      })

      //상대방이 내 기기를 제어한 경우 : 토글 완료된 상태를 응답해준다.
      if (event.from.connectionId !== this.myInfo.connectionId) {
        this.$call.sendFlashStatus(flashStatus, [event.from.connectionId])
      }

      //내 기기 제어지만, 전체공유 상태고, 내가 리더가 아닌 경우에는 플래시 상태를 리더에게 보내줘야 한다. (현재 플래시 on/off상태 싱크를 맞추기 위해)
      if (
        event.from.connectionId === this.myInfo.connectionId &&
        this.myInfo.roleType !== ROLE.LEADER &&
        this.viewForce
      ) {
        const leader = this.participants.find(
          user => user.roleType === ROLE.LEADER,
        )
        //리더에게 내 플래시 상태를 전송하여 싱크를 맞춘다
        if (leader)
          this.$call.sendFlashStatus(flashStatus, [leader.connectionId])
      }
    },
  },
  mounted() {
    if (!this.isFlashDisable) {
      this.$eventBus.$on(SIGNAL.FLASH, this.flashControlListener)
    }
  },
  beforeDestroy() {
    if (!this.isFlashDisable) {
      this.$eventBus.$on(SIGNAL.FLASH, this.flashControlListener)
    }
  },
}
