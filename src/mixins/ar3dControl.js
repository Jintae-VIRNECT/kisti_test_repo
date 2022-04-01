import Cookies from 'js-cookie'

import { AR_3D_CONTROL_TYPE } from 'configs/remote.config'
import { mapGetters, mapMutations } from 'vuex'

export default {
  computed: {
    ...mapGetters(['isControlPopOverVisible', 'controlActive', 'mainView']),
  },
  methods: {
    ...mapMutations([
      'SET_CONTROL_ACTIVE',
      'SET_IS_REQUEST_MODAL_VISIBLE',
      'SET_IS_CONTROL_POP_OVER_VISIBLE',
    ]),

    //제어 요청 팝업 or 제어 팝업 열기
    onClick3dControlBtn() {
      const toggleControlActive = !this.controlActive
      this.SET_CONTROL_ACTIVE(toggleControlActive)

      if (!toggleControlActive) {
        this.SET_IS_CONTROL_POP_OVER_VISIBLE(false) //제어 창 열린 경우 닫기
      }

      const doNotShowValueFromCookie = Cookies.get('doNotShow_controlRequest')

      //권한 요청 질의 팝업 열기
      if (toggleControlActive && !doNotShowValueFromCookie) {
        this.SET_IS_REQUEST_MODAL_VISIBLE(true)
      }
      //권한 요청 시그널 전송
      else if (toggleControlActive && doNotShowValueFromCookie) {
        this.requestSend() //권한 요청 시그널 전송, 요청 중 팝업 열기
      }
    },

    //요청 전송 중 팝업 표시
    requestSend() {
      this.$call.sendAr3dSharing(AR_3D_CONTROL_TYPE.REQUEST, {}, [
        this.mainView.connectionId,
      ]) //콘텐츠 제어 권한 요청 시그널 전송

      const confirm = {
        text: this.$t('button.cancel'),
        action: () => {
          this.SET_CONTROL_ACTIVE(false)

          //요청 수락 전 요청 취소 시그널 전송
          this.$call.sendAr3dSharing(AR_3D_CONTROL_TYPE.CANCEL_REQUEST, {}, [
            this.mainView.connectionId,
          ])
        },
      }

      const option = {
        backdrop: `rgba(0,0,0,0.6)`,
        allowOutsideClick: false,
      }

      //'요청 중' 팝업 표시
      this.confirmDefault(
        this.$t('service.3d_content_control_permission_requesting'),
        confirm,
        option,
      )
    },

    //콘텐츠 제어 권한 요청 질의 팝업 (취소)닫기
    onRequestCancel() {
      this.SET_CONTROL_ACTIVE(false)
    },

    reset3dControlStatus() {
      this.SET_IS_CONTROL_POP_OVER_VISIBLE(false)
      this.SET_CONTROL_ACTIVE(false)
      this.SET_IS_REQUEST_MODAL_VISIBLE(false)
    },
  },
}
