<template>
  <nav class="header-lnbs service">
    <ul class="flex">
      <lnb-button
        v-for="menu of menus"
        :key="menu.key"
        :text="menu.text"
        :active="view === menu.key"
        :image="menu.icon"
        :notice="menu.notice"
        @click="goTab(menu.key)"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import {
  DRAWING,
  SIGNAL,
  AR_FEATURE,
  CAPTURE_PERMISSION,
  ROLE,
} from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import LnbButton from '../tools/LnbButton'
import toastMixin from 'mixins/toast'
import configmMixin from 'mixins/confirm'
import { getResolutionScale } from 'utils/settingOptions'

// import web_test from 'utils/testing'
export default {
  name: 'HeaderServiceLnb',
  mixins: [toastMixin, configmMixin],
  components: {
    LnbButton,
  },
  data() {
    return {
      drawingNotice: 1,
      arNotice: 2,
      menus: [
        {
          text: this.$t('service.stream'),
          key: VIEW.STREAM,
          icon: require('assets/image/call/gnb_ic_shareframe.svg'),
          notice: false,
        },
        {
          text: this.$t('service.drawing'),
          key: VIEW.DRAWING,
          icon: require('assets/image/call/gnb_ic_creat_basic.svg'),
          notice: false,
        },
        {
          text: this.$t('service.ar'),
          key: VIEW.AR,
          icon: require('assets/image/call/gnb_ic_creat_ar.svg'),
          notice: false,
        },
      ],
    }
  },
  computed: {
    ...mapGetters([
      'mainView',
      'participants',
      'view',
      'shareFile',
      'viewForce',
      'settingInfo',
      'myInfo',
      'video',
    ]),
    hasLeader() {
      const idx = this.participants.findIndex(
        user => user.roleType === ROLE.LEADER,
      )
      if (idx < 0) return false
      return true
    },
  },
  watch: {
    shareFile(file, oldFile) {
      if (file && file.id && file.id !== oldFile.id) {
        this.addChat({
          name: file.fileName,
          status: 'drawing',
          type: 'system',
        })
        if (this.view !== VIEW.DRAWING) {
          this.menus[this.drawingNotice].notice = true
        }
      } else if (!file || !file.id) {
        this.menus[this.drawingNotice].notice = false
      }
    },
    hasLeader(val, bVal) {
      if (!val && val !== bVal && this.participants.length > 0) {
        this.toastDefault(this.$t('service.toast_leave_leader'))
        this.showImage({})
      }
    },
    viewForce() {
      this.controlScale()
    },
    participants: {
      handler() {
        this.controlScale()
      },
      deep: true,
    },

    mainView: {
      deep: true,
      handler(val, oldVal) {
        // AR 기능 도중 메인뷰 참가자가 나갔을 경우
        const isArView = this.view === VIEW.AR
        const isDiffMainView = val.id !== oldVal.id
        const isNotEmptyPt = oldVal.id !== undefined

        if (isArView && isDiffMainView && isNotEmptyPt) {
          if (this.account.roleType === ROLE.LEADER) {
            this.$call.sendArFeatureStop()
          }
          this.setView(VIEW.STREAM)
        }
      },
    },
  },
  methods: {
    ...mapActions(['setView', 'addChat', 'showImage']),
    ...mapMutations(['updateParticipant']),
    goTab(type) {
      if (type === this.view) return

      if (this.checkDisconnected()) {
        return
      }

      const isLeader = this.account.roleType === ROLE.LEADER
      isLeader ? this.goTabAsLeader(type) : this.goTabAsNotLeader(type)
    },
    /**
     * 리더일때
     * 1. AR 종료 or AR 요청 처리
     * 2. 다른 탭으로 이동
     */
    goTabAsLeader(type) {
      if (this.view === VIEW.AR) {
        this.showArExitConfirm(type)
      } else {
        if (type === VIEW.AR) {
          if (this.checkArRequestPermission()) {
            this.$call.sendCapturePermission([this.mainView.connectionId])
            this.toastDefault(this.$t('service.toast_request_permission'))
          }
        } else {
          this.setView(type)
        }
      }
    },
    /**
     * 일반 참가자일때
     * 1. AR 기능을 벗어날 수 있는지 확인
     * 2. AR 탭으로 이동 할 수 있는지 확인
     * 3. 다른 탭으로 이동
     */
    goTabAsNotLeader(type) {
      if (this.view === VIEW.AR) {
        this.toastDefault(this.$t('service.toast_cannot_leave_ar'))
        return
      }

      if (type === VIEW.AR) {
        this.toastDefault(this.$t('service.toast_cannot_invite_ar'))
        return
      }

      this.setView(type)
    },

    showArExitConfirm(type) {
      this.serviceConfirmTitle(
        this.$t('service.ar_exit'),
        this.$t('service.ar_exit_description'),
        {
          text: this.$t('button.exit'),
          action: () => {
            this.$call.sendArFeatureStop()
            this.setView(type)
          },
        },
      )
    },

    /**
     * AR 요청이 가능한지 확인
     */
    checkArRequestPermission() {
      // 웹-웹 테스트용
      // if (web_test) {
      //   this.setView('ar')
      //   return
      // }

      //전체공유중인 화면이 없으면
      if (this.viewForce === false) {
        this.toastDefault(this.$t('service.toast_no_sharing'))
        return false
      }

      //메인뷰가 없거나, 스트림이 없으면
      if (!this.mainView || !this.mainView.stream) {
        this.toastDefault(this.$t('service.toast_no_worker'))
        return false
      }

      //내가 내화면을 보고 있는 경우
      if (this.mainView.id === this.account.uuid) {
        this.toastDefault(this.$t('service.toast_unsupport_ar'))
        return false
      }

      //AR 기능이 없는 경우
      if (this.mainView.hasArFeature === false) {
        this.toastDefault(this.$t('service.toast_unsupport_ar'))
        return false
      }

      //메인뷰의 permission 항목이 false인경우(과거 요청했는데 거절 당한 경우)
      if (this.mainView.permission === false) {
        this.toastDefault(this.$t('service.toast_refused_ar'))
        return false
      }

      return true
    },

    handlePermissionRes(receive) {
      const data = JSON.parse(receive.data)

      if (data.from === this.account.uuid) return

      if (
        this.account.roleType === ROLE.LEADER &&
        data.type === CAPTURE_PERMISSION.RESPONSE
      ) {
        this.updateParticipant({
          connectionId: receive.from.connectionId,
          permission: data.isAllowed,
        })
        if (this.view !== VIEW.AR) {
          const sendSignal = true
          data.isAllowed ? this.startAr(sendSignal) : this.showRejected()
        }
      }
    },
    startAr(sendSignal = false) {
      this.toastDefault(
        this.$t('service.toast_ar_start', { name: this.mainView.nickname }),
      )

      this.addChat({
        status: 'ar-start',
        name: this.mainView.nickname,
        type: 'system',
      })

      if (sendSignal) {
        this.$call.sendArFeatureStart(this.mainView.id)
      }

      this.setView(VIEW.AR)
    },
    showRejected() {
      this.toastDefault(this.$t('service.toast_refused_ar'))
      this.addChat({
        status: 'ar-deny',
        type: 'system',
      })
    },

    checkArFeature(receive) {
      const data = JSON.parse(receive.data)

      if (data.from === this.account.uuid) return
      if (this.account.roleType === ROLE.LEADER) {
        if (data.type === AR_FEATURE.FEATURE) {
          if ('hasArFeature' in data) {
            this.updateParticipant({
              connectionId: receive.from.connectionId,
              hasArFeature: data.hasArFeature,
            })
            if (data.hasArFeature === false) {
              this.addChat({
                status: 'ar-unsupport',
                type: 'system',
              })
            }
          }
        }
      } else {
        if (data.type === AR_FEATURE.START_AR_FEATURE) {
          this.startAr()
        } else if (data.type === AR_FEATURE.STOP_AR_FEATURE) {
          this.toastDefault(this.$t('service.toast_ar_exit'))
          this.setView(VIEW.STREAM)
        }
      }
    },

    /**
     * 자신의 영상 스트림 스케일 제어
     */
    controlScale() {
      if (this.participants.length === 1) return

      const ptIndex = this.participants.findIndex(pt => {
        const isWatchingMe = pt.currentWatching === this.account.uuid

        //다른사람이 날 보고 있음.
        const watchingMeByElse = isWatchingMe && !pt.me

        //전체 공유로 인해 내가 내화면을 보고 있음.
        const watchingMeByForce =
          isWatchingMe && pt.me && this.viewForce === true
        return watchingMeByElse || watchingMeByForce
      })

      if (ptIndex > -1) {
        this.$call.setScaleResolution(1)
      } else {
        const quality = Number.parseInt(this.video.quality, 10)
        const scale = getResolutionScale(quality)
        this.$call.setScaleResolution(scale)
      }
    },

    //협업보드 공유 종료 메시지 수신 시
    receiveEndDrawing({ data }) {
      if (data.type === DRAWING.END_DRAWING) {
        this.toastDefault(this.$t('service.toast_drawing_end'))
        this.showImage({}) //공유중 파일 초기화
        this.setView(VIEW.STREAM) //탭 실시간 공유로 이동
      }
    },

    checkDisconnected() {
      //ice 커넥션이 끊어진 경우 메뉴 이동 불가
      const idx = this.participants.findIndex(
        user => user.me && user.status === 'disconnected',
      )
      if (idx >= 0) {
        this.toastError(this.$t('confirm.network_error'))
        return true
      } else {
        return false
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on(SIGNAL.CAPTURE_PERMISSION, this.handlePermissionRes)
    this.$eventBus.$on(SIGNAL.AR_FEATURE, this.checkArFeature)
    this.$eventBus.$on(SIGNAL.DRAWING, this.receiveEndDrawing)
  },

  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.CAPTURE_PERMISSION, this.handlePermissionRes)
    this.$eventBus.$off(SIGNAL.AR_FEATURE, this.checkArFeature)
    this.$eventBus.$off(SIGNAL.DRAWING, this.receiveEndDrawing)
  },
}
</script>
