<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="remote-wrapper service-wrapper">
      <transition name="subview">
        <sub-view v-show="chatBox || isScreenDesktop"></sub-view>
      </transition>

      <transition name="share">
        <share v-show="currentView === 'drawing'"></share>
      </transition>

      <main
        class="main-wrapper"
        :class="{ shareview: currentView === 'drawing' }"
      >
        <transition name="main">
          <stream-view
            :class="{
              hide: currentView !== 'stream' && currentView !== 'screen',
            }"
          ></stream-view>
        </transition>
        <transition name="main">
          <drawing-view v-show="currentView === 'drawing'"></drawing-view>
        </transition>
        <transition name="main">
          <ar-view v-show="currentView === 'ar'"></ar-view>
        </transition>
        <transition name="popover">
          <capture-modal
            v-if="captureFile.id"
            :file="captureFile"
          ></capture-modal>
        </transition>
      </main>

      <user-list :class="userListClass"></user-list>
      <!-- <div v-else>
        <figure
          v-for="participant of participants"
          :key="'audio_' + participant.id"
        >
          <audio
            v-if="!participant.me && participant.hasAudio"
            :srcObject.prop="participant.stream"
            autoplay
            playsinline
            loop
          ></audio>
        </figure>
      </div> -->
      <!-- <component :is="viewComponent"></component> -->
    </div>
    <reconnect-modal :visible.sync="connectVisible"></reconnect-modal>
    <setting-modal></setting-modal>
    <record-list v-if="useLocalRecording"></record-list>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import SubView from './subview/SubView'
import UserList from './participants/ParticipantList'
import { ROLE } from 'configs/remote.config'
import { CAMERA } from 'configs/device.config'
import { VIEW } from 'configs/view.config'
import localRecorderMixin from 'mixins/localRecorder'
import serverRecordMixin from 'mixins/serverRecorder'
import Store from 'stores/remote/store'
import confirmMixin from 'mixins/confirm'
import { checkInput } from 'utils/deviceCheck'
import ReconnectModal from './modal/ReconnectModal'

import { mapGetters } from 'vuex'
export default {
  name: 'ServiceLayout',
  beforeRouteEnter(to, from, next) {
    if (from.name !== 'workspace') {
      next({ name: 'workspace' })
    }
    next()
  },
  beforeRouteLeave(to, from, next) {
    Store.commit('callClear')
    Store.dispatch('callReset')
    next()
  },
  mixins: [localRecorderMixin, serverRecordMixin, confirmMixin],
  components: {
    ReconnectModal,
    HeaderSection,
    SubView,
    UserList,
    StreamView: () => import('./ServiceStream'),
    DrawingView: () => import('./ServiceDrawing'),
    ArView: () => import('./ServiceAr'),
    Share: () => import('./share/Share'),
    CaptureModal: () => import('./modal/CaptureModal'),
    RecordList: () => import('LocalRecordList'),
    SettingModal: () => import('./modal/SettingModal'),
  },
  data() {
    return {
      showDenied: false,
      callTimeout: null,
      isFullScreen: false,
      connectVisible: false,
      isVideoLoaded: false,
    }
  },
  computed: {
    ...mapGetters([
      'view',
      'captureFile',
      'chatBox',
      'myInfo',
      'video',
      'restrictedRoom',
      'useLocalRecording',
    ]),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
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
    userListClass() {
      return {
        shareview: this.currentView === 'drawing',
        fullscreen:
          this.isVideoLoaded &&
          this.isFullScreen &&
          (this.currentView === 'stream' || this.currentView === 'screen'),
      }
    },
  },

  methods: {
    changeOrientation(event) {
      if (!this.myInfo || !this.myInfo.stream) return
      const tracks = this.myInfo.stream.getVideoTracks()
      if (tracks.length === 0) return
      setTimeout(() => {
        const track = tracks[0]
        const settings = track.getSettings()
        this.logger('call', `resolution::${settings.width}X${settings.height}`)
        this.$call.sendResolution({
          width: settings.width,
          height: settings.height,
          orientation:
            window.orientation === 90 || window.orientation === -90
              ? 'landscape-primary'
              : 'portrait-primary',
        })
      }, 3000)
    },
    showTimeoutConfirm() {
      if (this.callTimeout) {
        clearTimeout(this.callTimeout)
        this.callTimeout = null
      }
      this.callTimeout = setTimeout(() => {
        this.logout()
      }, 30 * 1000)
      this.confirmCancel(
        this.$t('confirm.call_timeout'),
        {
          text: this.$t('button.progress'),
          action: this.initTimeout,
        },
        {
          text: this.$t('button.exit_room'),
          action: this.logout,
        },
      )
    },
    initTimeout() {
      if (this.callTimeout) {
        clearTimeout(this.callTimeout)
        this.callTimeout = null
      }
      this.confirmClose()
      this.callTimeout = setTimeout(() => {
        this.showTimeoutConfirm()
      }, 60 * 60 * 1000)
    },
    logout() {
      if (this.callTimeout) {
        clearTimeout(this.callTimeout)
      }

      this.confirmClose()
      this.callTimeout = null

      this.$eventBus.$emit('call:logout')
    },
    async onDeviceChange() {
      const { hasVideo } = await checkInput({ video: true, audio: false })
      if (hasVideo === (this.myInfo.cameraStatus !== CAMERA.CAMERA_NONE)) return
      this.$call.sendCamera(
        hasVideo
          ? this.video.isOn
            ? CAMERA.CAMERA_ON
            : CAMERA.CAMERA_OFF
          : CAMERA.CAMERA_NONE,
      )
    },
    setFullScreen(flag) {
      this.isFullScreen = flag
    },
    reconnect(event) {
      if (event.reason === 'networkDisconnect') {
        this.connectVisible = true
      }
    },
    setVideoLoaded(flag) {
      this.isVideoLoaded = flag
    },
  },

  /* Lifecycles */
  async created() {
    if (!this.$call.session) {
      this.$router.push({ name: 'workspace' })
      return
    }
    this.initTimeout()
    window.onbeforeunload = () => {
      return true
    }
    navigator.mediaDevices.ondevicechange = this.onDeviceChange
    window.addEventListener('keydown', this.stopLocalRecordByKeyPress)
    window.addEventListener('orientationchange', this.changeOrientation)
    this.$call.addListener('sessionDisconnected', this.reconnect)
    this.$eventBus.$on('video:fullscreen', this.setFullScreen)
    this.$eventBus.$on('video:loaded', this.setVideoLoaded)
  },
  beforeDestroy() {
    if (this.callTimeout) {
      clearTimeout(this.callTimeout)
    }

    if (this.recordingId && this.recordTimeout) {
      this.clearServerRecordTimer()
    }

    window.onbeforeunload = () => {}
    navigator.mediaDevices.ondevicechange = () => {}
    window.removeEventListener('keydown', this.stopLocalRecordByKeyPress)
    window.removeEventListener('orientationchange', this.changeOrientation)

    this.stopLocalRecord()
    this.setServerRecordStatus('STOP')

    this.$eventBus.$off('video:fullscreen', this.setFullScreen)
    this.$eventBus.$off('video:loaded', this.setVideoLoaded)
  },

  mounted() {
    if (this.restrictedRoom) {
      this.toastDefault(this.$t('service.toast_video_restrict_mode'))
    }
  },
}
</script>

<style lang="scss" src="assets/style/service.scss"></style>

<style lang="scss">
@import '~assets/style/vars';
.share-enter-active,
.share-leave-active {
  transition: transform ease 0.3s;
}
.share-enter {
  transform: translateX(-#{$share_width});
}
.share-enter-to {
  transform: translateX(0);
}
.share-leave {
  transform: translateX(0);
}
.share-leave-to {
  transform: translateX(-#{$share_width});
}

.main-enter-active {
  transition: opacity ease 0.2s 0.2s;
}
.main-leave-active {
  transition: opacity ease 0.2s;
}
.main-enter {
  opacity: 0;
}
.main-enter-to {
  opacity: 1;
}
.main-leave {
  opacity: 1;
}
.main-leave-to {
  opacity: 0;
}

.subview-enter-active,
.subview-leave-active {
  transition: transform ease 0.3s;
}
.subview-enter,
.subview-leave-to {
  transform: translateX(100%);
}
.subview-enter-to,
.subview-leave {
  transform: translateX(0);
}
</style>
