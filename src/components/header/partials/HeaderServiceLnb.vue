<template>
  <nav class="header-lnbs service">
    <ul class="flex">
      <lnb-button
        :text="$t('service.stream')"
        :active="currentView === 'stream'"
        :image="require('assets/image/call/gnb_ic_shareframe.svg')"
        @click="goTab('stream')"
      ></lnb-button>
      <lnb-button
        :text="$t('service.drawing')"
        :active="currentView === 'drawing'"
        :notice="drawingNotice"
        :image="require('assets/image/call/gnb_ic_creat_basic.svg')"
        @click="goTab('drawing')"
      ></lnb-button>
      <lnb-button
        :text="$t('service.ar')"
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
  DRAWING,
  AR_FEATURE,
  CAPTURE_PERMISSION,
  ROLE,
} from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import LnbButton from '../tools/LnbButton'
import toastMixin from 'mixins/toast'
import configmMixin from 'mixins/confirm'
// import web_test from 'utils/testing'
export default {
  name: 'HeaderServiceLnb',
  mixins: [toastMixin, configmMixin],
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
        user => user.roleType === ROLE.LEADER,
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
      if (file && file.id && file.id !== oldFile.id) {
        this.addChat({
          name: file.fileName,
          status: 'drawing',
          type: 'system',
        })
        if (this.currentView !== 'drawing') {
          this.drawingNotice = true
        }
      }
    },
    hasLeader(hear, bHear) {
      if (!hear && hear !== bHear && this.participants.length > 0) {
        this.toastDefault(this.$t('service.toast_leave_leader'))
        this.setView(VIEW.STREAM)
      }
    },
    hasWorker(hear, bHear) {
      if (!hear && hear !== bHear && this.participants.length > 0) {
        this.toastDefault(this.$t('service.toast_leave_worker'))
        if (this.view === VIEW.AR) {
          this.setView(VIEW.STREAM)
        }
      }
    },
  },
  methods: {
    ...mapActions(['setView', 'addChat']),
    ...mapMutations(['updateParticipant']),
    goTab(type) {
      if (type === this.currentView) return

      // leader
      if (this.account.roleType === ROLE.LEADER) {
        if (this.currentView === 'ar') {
          // TODO: MESSAGE
          this.confirmCancel(this.$t('service.toast_exit_ar'), {
            text: this.$t('button.exit'),
            action: () => {
              this.$call.arFeature(AR_FEATURE.STOP_AR_FEATURE)
              this.goTabConfirm(type)
            },
          })
          return
        }
        if (this.currentView === 'drawing') {
          if (this.shareFile && this.shareFile.id) {
            // TODO: MESSAGE
            this.confirmCancel(this.$t('service.toast_exit_drawing'), {
              text: this.$t('button.exit'),
              action: () => {
                this.$call.drawing(DRAWING.END_DRAWING)
                this.goTabConfirm(type)
              },
            })
            return
          }
        }
        this.goTabConfirm(type)
      } // other user
      else {
        if (this.currentView === VIEW.AR) {
          this.toastDefault(this.$t('service.toast_cannot_leave_ar'))
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
            this.toastDefault(this.$t('service.toast_cannot_invite_drawing'))
          }
          this.goDrawing()
        }
        if (type === 'ar') {
          if (!this.arNotice) {
            this.toastDefault(this.$t('service.toast_cannot_invite_ar'))
            return
          }
        }
      }
    },
    goTabConfirm(type) {
      if (type === 'stream') {
        this.setView(VIEW.STREAM)
      }
      if (type === 'drawing') {
        this.setView(VIEW.DRAWING)
      }
      if (type === 'ar') {
        this.permissionCheck()
      }
    },
    goDrawing() {
      if (this.account.roleType === ROLE.LEADER) {
        this.setView(VIEW.DRAWING)
        return
      }
      if (this.shareFile && this.shareFile.id) {
        this.setView(VIEW.DRAWING)
      } else {
        this.toastDefault(this.$t('service.toast_cannot_invite_drawing'))
      }
    },
    permissionSetting(permission) {
      if (permission === true) {
        this.$call.arFeature(AR_FEATURE.START_AR_FEATURE)
        this.setView(VIEW.AR)
      } else if (permission === false) {
        this.toastDefault(this.$t('service.toast_refused_ar'))
        this.addChat({
          status: 'ar-deny',
          type: 'system',
        })
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
        this.toastDefault(this.$t('service.toast_no_worker'))
        return
      }
      if (this.mainView.id === this.account.uuid) {
        console.error(this.$t('service.toast_current_stream'))
        return
      }
      if (this.mainView.hasArFeature === false) {
        this.toastDefault(this.$t('service.toast_unsupport_ar'))
        return
      }
      if (this.mainView.permission === false) {
        this.toastDefault(this.$t('service.toast_refused_ar'))
        return
      }
      this.$call.permission({
        to: this.mainView.id,
      })
      this.toastDefault(this.$t('service.toast_request_permission'))
    },

    getPermissionCheck(receive) {
      const data = JSON.parse(receive.data)

      // if (data.to !== this.account.uuid) return
      if (data.from === this.account.uuid) return

      if (
        this.account.roleType === ROLE.LEADER &&
        data.type === CAPTURE_PERMISSION.RESPONSE
      ) {
        this.updateParticipant({
          connectionId: receive.from.connectionId,
          permission: data.isAllowed,
        })
        this.permissionSetting(data.isAllowed)
      }
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
          // TODO: MESSAGE
          this.toastDefault(this.$t('service.toast_ar_start'))
          this.setView(VIEW.AR)
        } else if (data.type === AR_FEATURE.STOP_AR_FEATURE) {
          // TODO: MESSAGE
          this.toastDefault(this.$t('service.toast_ar_exit'))
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
