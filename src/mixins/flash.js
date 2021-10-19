import { FLASH } from 'configs/device.config'
import toastMixin from 'mixins/toast'
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'

export default {
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['mainView', 'viewForce', 'myInfo']),
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
    status() {
      return this.flashStatus === FLASH.FLASH_ON
    },
    disable() {
      return (
        this.flashStatus === FLASH.FLASH_NONE ||
        this.flashStatus === FLASH.NO_PERMISSION ||
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
    // ...mapMutations(['deviceUpdate']),
    clickHandler() {
      if (this.flashStatus === FLASH.FLASH_NONE) {
        this.toastDefault(this.$t('service.flash_none'))
        return
      }

      if (this.flashStatus === FLASH.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
        return
      }
      const toStatus = !this.status

      //전체 공유, 리더권한 인 경우
      if (this.viewForce && this.isLeader) {
        //자신의 플래시 제어
        if (this.mainView.id === this.myInfo.id) {
          //TODO: 자신의 플래시 제어
          //시그널 전송
          this.$call.sendFlash(toStatus, [this.myInfo.connectionId])
        } else {
          this.$call.sendFlash(toStatus, [this.mainView.connectionId])
        }
      }
      //자신의 플래시 제어
      else {
        //TODO: 자신의 플래시 제어
        //시그널 전송
        this.$call.sendFlash(toStatus, [this.myInfo.connectionId])
      }
    },
    flashListener(status, bStatus) {
      // 응답
      if (parseInt(status) === FLASH.CAMERA_ZOOMING) {
        this.toastNotice(this.$t('service.flash_controlling'))
        return
      }
      if (parseInt(status) === FLASH.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
      }
      // if (parseInt(status) === FLASH.FLASH_NONE) {
      //   this.toastDefault(this.$t('service.flash_none'))
      // }
      if (
        parseInt(status) === FLASH.FLASH_ON &&
        parseInt(bStatus) === FLASH.FLASH_OFF
      ) {
        this.toastDefault(this.$t('service.flash_on'))
      }
      if (
        parseInt(status) === FLASH.FLASH_OFF &&
        parseInt(bStatus) === FLASH.FLASH_ON
      ) {
        this.toastDefault(this.$t('service.flash_off'))
      }
      if (parseInt(status) === FLASH.APP_IS_BACKGROUND) {
        this.toastDefault(this.$t('service.flash_app_disable'))
      }
    },
  },
}
