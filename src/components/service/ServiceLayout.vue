<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="remote-wrapper service-wrapper">
      <transition name="subview">
        <sub-view v-show="chatBox || isScreenDesktop"></sub-view>
      </transition>

      <transition name="share">
        <!-- 협업보드 파일/공유 목록 -->
        <share v-if="!isMobileSize" v-show="currentView === 'drawing'"></share>
      </transition>

      <transition name="share">
        <!-- 3d공유 파일 목록 ui : 리더만 표시-->
        <share-3d
          v-if="!isMobileSize && isLeader && currentView === 'ar'"
          v-show="viewAction === ACTION.AR_3D"
        ></share-3d>
      </transition>

      <main
        class="main-wrapper"
        :class="{
          shareview: currentView === 'drawing' || isLeaderAr3dContentsMode,
        }"
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

      <user-list
        v-if="!isMobileSize"
        :class="userListClass"
        @openInviteModal="toggleInviteModal"
      ></user-list>
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
    <mobile-footer
      v-if="isMobileSize"
      @getFileList="onGetFileList"
      @openFileListModal="onOpenFileListModal"
      @get3dFileList="onGet3dFileList"
      @open3dFileListModal="onOpen3dFileListModal"
      @participantModalShow="onParticipantModalShow"
      @addPdfHistory="mobileAddPdfHistory"
      @uploading="onUploading"
      @uploaded="onUploaded"
    ></mobile-footer>
    <reconnect-modal :visible.sync="connectVisible"></reconnect-modal>
    <setting-modal></setting-modal>
    <record-list v-if="useLocalRecording"></record-list>

    <!-- pc에서만 지원 -->
    <map-modal
      v-if="isOnpremise && !isMobileSize"
      :visible.sync="positionMapVisible"
    ></map-modal>

    <!-- PC, 모바일 공통 사용 -->
    <guest-invite-modal :visible.sync="guestInviteModalVisible">
    </guest-invite-modal>
    <invite-modal :visible.sync="inviteModalVisible"></invite-modal>

    <!-- 모바일에서만 필요 -->
    <mobile-participant-modal
      v-if="isMobileSize"
      :modalShow.sync="isParticipantModalShow"
    ></mobile-participant-modal>
    <mobile-share-file-list-modal
      v-if="isMobileSize"
      ref="file-list"
      :modalShow.sync="isFileListModalShow"
      :fileList="fileList"
      :uploadingFile="uploadingFile"
      @rendered="onFileListRendered"
    ></mobile-share-file-list-modal>
    <mobile-3d-file-list-modal
      v-if="isMobileSize"
      :modalShow.sync="is3dFileListModalShow"
      :uploadingFile="uploadingFile"
      :fileList="ar3dFileList"
      @3dFileListUpdate="on3dFileListUpdate"
    ></mobile-3d-file-list-modal>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import SubView from './subview/SubView'
import UserList from './participants/ParticipantList'
import { ROLE } from 'configs/remote.config'
import { CAMERA } from 'configs/device.config'
import { VIEW, ACTION } from 'configs/view.config'
import { URLS } from 'configs/env.config'
import localRecorderMixin from 'mixins/localRecorder'
import serverRecordMixin from 'mixins/serverRecorder'
import Store from 'stores/remote/store'
import confirmMixin from 'mixins/confirm'
import { checkInput } from 'utils/deviceCheck'
import ReconnectModal from './modal/ReconnectModal'

import { mapGetters, mapActions, mapMutations } from 'vuex'
export default {
  name: 'ServiceLayout',
  beforeRouteEnter(to, from, next) {
    if (from.name !== 'workspace' && from.name !== 'connectioninfo') {
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
    MapModal: () => import('./modal/PositionMapModal'),
    MobileFooter: () => import('./tools/MobileFooter'),
    GuestInviteModal: () => import('./modal/GuestInviteModal'),
    InviteModal: () => import('./modal/InviteModal'),
    MobileParticipantModal: () => import('./modal/MobileParticipantModal'),
    MobileShareFileListModal: () => import('./modal/MobileShareFileListModal'),
    Mobile3dFileListModal: () => import('./modal/MobileShareFileListModal'),
    Share3d: () => import('./ar/3dcontents/Share3D'),
  },
  data() {
    return {
      ACTION: Object.freeze(ACTION),

      showDenied: false,
      callTimeout: null,
      isFullScreen: false,
      connectVisible: false,
      isVideoLoaded: false,
      positionMapVisible: false,
      guestInviteModalVisible: false,
      inviteModalVisible: false,

      fileList: [],
      isFileListModalShow: false,
      isParticipantModalShow: false,
      fileListCallback: () => {},

      is3dFileListModalShow: false,
      ar3dFileList: [],

      uploadingFile: '',
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
      'coworkTimeout',
      'viewAction',
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
    isLeaderAr3dContentsMode() {
      return (
        this.isLeader &&
        this.currentView === 'ar' &&
        this.viewAction === ACTION.AR_3D
      )
    },
  },
  watch: {
    //채팅창이 보이게 되는 경우 chat아이콘의 notice 제거
    isScreenDesktop(newVal) {
      if (newVal) {
        this.SET_CHAT_ACTIVE(false)
      }
    },
  },
  methods: {
    ...mapMutations(['SET_CHAT_ACTIVE']),
    ...mapActions(['useStt', 'setTool']),
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
      //30초후 자동 로그아웃
      this.callTimeout = setTimeout(() => {
        this.logout()
      }, 30 * 1000)

      this.confirmCancel(
        this.$t('confirm.call_timeout', { time: this.coworkTimeout }),
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
    //협업 시간 카운트 & 협업 연장 질의 팝업 생성
    initTimeout() {
      this.debug(`cowork timeout(min) : ${this.coworkTimeout}`)

      //timeout 0 무제한으로 설정시 협업 연장 질의 하지 않음
      if (this.coworkTimeout === 0) return

      if (this.callTimeout) {
        clearTimeout(this.callTimeout)
        this.callTimeout = null
      }

      this.confirmClose()
      this.callTimeout = setTimeout(() => {
        this.showTimeoutConfirm()
      }, this.coworkTimeout * 60 * 1000) //min * sec * ms
      //companyInfo의 설정 값에 따라 협업 연장 팝업 생성 사이클이 정해진다.
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
        if (this.account.roleType === ROLE.GUEST) {
          location.href = `${URLS['console']}/?continue=${location.href}`
          return
        } else {
          this.connectVisible = true
        }
      }
    },
    setVideoLoaded(flag) {
      this.isVideoLoaded = flag
    },
    togglePositionMap(flag) {
      this.positionMapVisible = flag
    },
    toggleGuestInvite(flag) {
      this.guestInviteModalVisible = flag
    },
    toggleInviteModal(flag) {
      this.inviteModalVisible = flag
    },
    onGetFileList(fileList) {
      this.fileList = fileList
    },
    onOpenFileListModal() {
      this.isFileListModalShow = true
    },
    onGet3dFileList(fileList) {
      this.ar3dFileList = fileList
    },
    onOpen3dFileListModal() {
      this.is3dFileListModalShow = true
    },
    onParticipantModalShow() {
      this.isParticipantModalShow = true
    },
    mobileAddPdfHistory(data) {
      //mobile-share-file-list-modal 컴포넌트가 생성되기 전에 호출 되므로,
      //아직 컴포넌트 dom이 추가되지 않았다면 callback으로 등록시켜 논 후 해당 컴포넌트가 랜더링 된 후
      //이벤트수신 시 콜백을 실행하도록 한다.
      if (this.$refs['file-list']) this.$refs['file-list'].addPdfHistory(data)
      else
        this.fileListCallback = () => {
          this.fileListCallback = () => {}
          this.$refs['file-list'].addPdfHistory(data)
        }
    },
    onFileListRendered() {
      this.fileListCallback()
    },
    on3dFileListUpdate(fileList) {
      this.ar3dFileList = fileList
    },
    onUploading(fileName) {
      this.uploadingFile = fileName
    },
    onUploaded() {
      this.uploadingFile = ''
    },
  },

  /* Lifecycles */
  async created() {
    if (!this.$call.session) {
      this.$router.push({ name: 'workspace' })
      return
    }

    //드로잉 도구 정보를 셋팅
    const drawingInfo = window.myStorage.getItem('drawingInfo')
    if (drawingInfo) {
      for (const key in drawingInfo) {
        this.setTool({
          target: key,
          value: drawingInfo[key],
        })
      }
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
    this.$eventBus.$on('map:show', this.togglePositionMap)
    this.$eventBus.$on('guestInvite:show', this.toggleGuestInvite)
    this.$eventBus.$on('inviteModal:show', this.toggleInviteModal)
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
    this.$eventBus.$off('map:show', this.togglePositionMap)
    this.$eventBus.$off('guestInvite:show', this.toggleGuestInvite)
    this.$eventBus.$off('inviteModal:show', this.toggleInviteModal)

    //협업 종료시 stt 종료
    this.useStt(false)
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
