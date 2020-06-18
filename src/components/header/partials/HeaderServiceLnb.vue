<template>
  <nav class="header-lnb service">
    <ul class="flex">
      <lnb-button
        text="실시간 공유"
        :active="currentView === 'stream'"
        :image="require('assets/image/call/gnb_ic_shareframe.svg')"
        @click="setView('stream')"
      ></lnb-button>
      <lnb-button
        text="협업 보드"
        :active="currentView === 'drawing'"
        keyvalue="drawing"
        :notice="drawingNotice"
        :image="require('assets/image/call/gnb_ic_creat_basic.svg')"
        @click="goDrawing"
      ></lnb-button>
      <lnb-button
        text="AR 기능"
        :active="currentView === 'ar'"
        :notice="arNotice"
        :image="require('assets/image/call/gnb_ic_creat_ar.svg')"
        @click="permissionCheck"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { SIGNAL, ROLE } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import LnbButton from '../tools/LnbButton'
import toastMixin from 'mixins/toast'
import web_test from 'utils/testing'
export default {
  name: 'HeaderServiceLnb',
  mixins: [toastMixin],
  components: {
    LnbButton,
  },
  data() {
    return {
      drawingNotice: false,
      arNotice: false,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'participants', 'view', 'shareFile']),
    currentView() {
      if (this.view === VIEW.STREAM) {
        return 'stream'
      } else if (this.view === VIEW.DRAWING) {
        return 'drawing'
      } else if (this.view === VIEW.AR) {
        return 'ar'
      }
      return ''
    },
  },
  watch: {
    'mainView.permission': 'permissionSetting',
    shareFile(file, oldFile) {
      if (
        file &&
        file.id &&
        file.id !== oldFile.id &&
        this.currentView !== 'drawing'
      ) {
        this.drawingNotice = true
      }
    },
  },
  methods: {
    ...mapActions(['setView']),
    goDrawing() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        this.setView(VIEW.DRAWING)
        return
      }
      if (this.drawingNotice) {
        this.drawingNotice = false
      }
      if (this.shareFile && this.shareFile.id) {
        this.setView(VIEW.DRAWING)
      } else {
        this.toastDefault('협업 보드가 활성화되어 있지 않습니다.')
      }
    },
    permissionSetting(permission) {
      if (permission === true) {
        this.setView('ar')
      } else if (permission === false) {
        this.toastDefault(
          '상대방이 AR 기능을 거절했습니다. 통화를 다시 수립해야 AR 기능을 사용할 수 있습니다.',
        )
      }
    },
    permissionCheck() {
      // 웹-웹 테스트용
      // if (web_test) {
      //   this.setView('ar')
      //   return
      // }
      if (!this.mainView || !this.mainView.stream) {
        // TODO: MESSAGE
        this.toastDefault(
          '작업자가 존재하지 않습니다.?? 이 경우 메시지 정의 필요',
        )
        return
      }
      if (this.mainView.id === this.account.uuid) {
        console.error('본인 영상입니다.')
        return
      }
      if (this.mainView.permission === true) {
        this.setView('ar')
        return
      }
      if (this.mainView.permission === false) {
        this.toastDefault(
          '상대방이 AR 기능을 거절했습니다. 통화를 다시 수립해야 AR 기능을 사용할 수 있습니다.',
        )
        return
      }
      if (this.mainView.permission === 'noAR') {
        this.toastDefault('AR 기능을 사용할 수 없는 장치입니다.')
        return
      }
      this.toastDefault(
        '상대방에게 AR 기능 허가를 요청했습니다. 잠시만 기다려주세요.',
      )

      this.$call.permission({
        to: this.mainView.id,
      })
    },
    // 웹-웹 테스트용!!!
    getPermissionCheck(receive) {
      const data = JSON.parse(receive.data)

      if (data.to !== this.account.uuid) return

      if (!('value' in data)) {
        this.$call.permission({
          to: data.from,
          value: true,
        })
      }
    },
  },

  /* Lifecycles */
  created() {
    if (web_test) {
      // 웹-웹 테스트용!!!
      this.$call.addListener(SIGNAL.CAPTURE_PERMISSION, this.getPermissionCheck)
    }
  },
}
</script>
