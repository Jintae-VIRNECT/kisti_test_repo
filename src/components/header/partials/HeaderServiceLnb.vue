<template>
  <nav class="header-lnbs service">
    <ul class="flex">
      <lnb-button
        text="실시간 공유"
        :active="currentView === 'stream'"
        :image="require('assets/image/call/gnb_ic_shareframe.svg')"
        @click="goTab('stream')"
      ></lnb-button>
      <lnb-button
        text="협업 보드"
        :active="currentView === 'drawing'"
        :notice="drawingNotice"
        :image="require('assets/image/call/gnb_ic_creat_basic.svg')"
        @click="goTab('drawing')"
      ></lnb-button>
      <lnb-button
        text="AR 기능"
        :active="currentView === 'ar'"
        :notice="arNotice"
        :image="require('assets/image/call/gnb_ic_creat_ar.svg')"
        @click="goTab('ar')"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import {
  SIGNAL,
  AR_FEATURE,
  CAPTURE_PERMISSION,
  ROLE,
} from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import LnbButton from '../tools/LnbButton'
import toastMixin from 'mixins/toast'
// import web_test from 'utils/testing'
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
    hasLeader() {
      const idx = this.participants.findIndex(
        user => user.roleType === ROLE.EXPERT_LEADER,
      )
      if (idx < 0) return false
      return true
    },
    hasWorker() {
      const idx = this.participants.findIndex(
        user => user.roleType === ROLE.WORKER,
      )
      if (idx < 0) return false
      return true
    },
  },
  watch: {
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
    hasLeader(hear, bHear) {
      if (!hear && hear !== bHear) {
        this.toastDefault('리더가 협업을 종료했습니다.')
        this.setView(VIEW.STREAM)
      }
    },
    hasWorker(hear, bHear) {
      if (!hear && hear !== bHear) {
        this.toastDefault('작업자가 협업을 종료했습니다.')
        if (this.view === VIEW.AR) {
          this.setView(VIEW.STREAM)
        }
      }
    },
  },
  methods: {
    ...mapActions(['setView']),
    ...mapMutations(['updateParticipant']),
    goTab(type) {
      if (type === this.currentView) return

      // leader
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        if (this.currentView === 'ar') {
          this.$call.arFeature(AR_FEATURE.STOP_AR_FEATURE)
        }
        if (type === 'stream') {
          this.setView(VIEW.STREAM)
        }
        if (type === 'drawing') {
          this.setView(VIEW.DRAWING)
        }
        if (type === 'ar') {
          this.permissionCheck()
        }
      } // other user
      else {
        if (this.currentView === VIEW.AR) {
          this.toastDefault('AR 공유 중에는 다른 메뉴로 이동할 수 없습니다.')
          return
        }
        if (type === 'stream') {
          this.setView(VIEW.STREAM)
        }
        if (type === 'drawing') {
          if (this.shareFile && this.shareFile.id) {
            this.drawingNotice = false
            this.setView(VIEW.DRAWING)
          } else {
            this.toastDefault('협업 보드가 활성화되어 있지 않습니다.')
          }
          this.goDrawing()
        }
        if (type === 'ar') {
          if (!this.arNotice) {
            this.toastDefault('AR 공유가 활성화되어 있지 않습니다.')
            return
          }
        }
      }
    },
    goDrawing() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        this.setView(VIEW.DRAWING)
        return
      }
      if (this.shareFile && this.shareFile.id) {
        this.setView(VIEW.DRAWING)
      } else {
        this.toastDefault('협업 보드가 활성화되어 있지 않습니다.')
      }
    },
    permissionSetting(permission) {
      console.log('PERMISSION CHECK', permission)
      if (permission === true) {
        this.$call.arFeature(AR_FEATURE.START_AR_FEATURE)
        this.setView(VIEW.AR)
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
      if (this.mainView.hasArFeature === false) {
        this.toastDefault('AR 기능을 사용할 수 없는 장치입니다.')
        return
      }
      if (this.mainView.permission === false) {
        this.toastDefault(
          '상대방이 AR 기능을 거절했습니다. 통화를 다시 수립해야 AR 기능을 사용할 수 있습니다.',
        )
        return
      }
      this.$call.permission({
        to: this.mainView.id,
      })
      this.toastDefault(
        '상대방에게 AR 기능 허가를 요청했습니다. 잠시만 기다려주세요.',
      )
    },

    getPermissionCheck(receive) {
      const data = JSON.parse(receive.data)

      // if (data.to !== this.account.uuid) return
      if (data.from === this.account.uuid) return

      // 웹-웹 테스트용!!!
      // if (web_test && data.type === CAPTURE_PERMISSION.REQUEST) {
      //   this.$call.permission({
      //     to: data.from,
      //     type: CAPTURE_PERMISSION.RESPONSE,
      //     isAllowed: true,
      //   })
      //   return
      // }
      if (
        this.account.roleType === ROLE.EXPERT_LEADER &&
        data.type === CAPTURE_PERMISSION.RESPONSE
      ) {
        this.updateParticipant({
          connectionId: data.from.connectionId,
          permission: data.isAllowed,
        })
        this.permissionSetting(data.isAllowed)
      }
    },

    checkArFeature(receive) {
      const data = JSON.parse(receive.data)

      if (data.from === this.account.uuid) return
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        if (data.type === AR_FEATURE.HAS_AR_FEATURE) {
          this.updateParticipant({
            connectionId: data.from.connectionId,
            arFeature: data.hasArFeature,
          })
        }
      } else {
        if (data.type === AR_FEATURE.START_AR_FEATURE) {
          // TODO: MESSAGE
          this.toastDefault('리더가 AR 공유를 시작했습니다.')
          this.setView(VIEW.AR)
        } else if (data.type === AR_FEATURE.STOP_AR_FEATURE) {
          // TODO: MESSAGE
          this.toastDefault('리더가 AR 공유를 종료했습니다.')
          this.setView(VIEW.STREAM)
        }
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$call.addListener(SIGNAL.CAPTURE_PERMISSION, this.getPermissionCheck)
    this.$call.addListener(SIGNAL.AR_FEATURE, this.checkArFeature)
  },
}
</script>
