<template>
  <article @mouseenter="hoverContents" @mouseleave="leaveContents">
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      @dblclick="changeMain"
      @touchstart="touch"
      @touchend="touchEnd"
    >
      <div class="participant-video__stream" v-if="participant.hasVideo">
        <video
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
          :muted="isMuted"
          ref="participant-video"
        ></video>
        <pano-video
          v-if="participant.streamMode"
          targetRef="participant-video"
          :connectionId="participant.connectionId"
          type="sub"
        ></pano-video>
      </div>
      <div class="participant-video__profile" v-else>
        <img
          v-if="participant.path && participant.path !== 'default'"
          class="participant-video__profile-background"
          :src="profileUrl"
          @error="onImageError"
        />
        <div class="participant-video__profile-dim"></div>
        <profile
          :thumbStyle="{
            width: '4.571rem',
            height: '4.571rem',
            margin: '10px auto 0',
          }"
          :image="participant.path"
        ></profile>
        <audio
          v-if="!participant.me"
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
          :muted="isMuted"
        ></audio>
      </div>
      <div class="participant-video__mute" v-if="participant.mute"></div>
      <div class="participant-video__status">
        <div class="participant-video__network" :class="participant.status">
          <!-- <div
            class="participant-video__network-hover"
            :class="{ hover: hover }"
            :style="statusHover"
          >
            <span :class="participant.status"
              >{{ $t('service.participant_network') }} :
              {{ participant.status | networkStatus }}</span
            >
          </div> -->
        </div>
        <span class="participant-video__leader" v-if="isLeader">
          Leader
        </span>
      </div>
      <div class="participant-video__device">
        <img
          v-if="participant.hasVideo && !participant.video"
          src="~assets/image/call/ic_video_off.svg"
        />
        <template v-if="!isMe">
          <img
            v-if="participant.hasAudio"
            :src="
              participant.audio
                ? require('assets/image/ic_mic_on.svg')
                : require('assets/image/ic_mic_off.svg')
            "
          />
          <img
            :src="
              participant.speaker
                ? require('assets/image/ic_volume_on.svg')
                : require('assets/image/ic_volume_off.svg')
            "
          />
        </template>
      </div>
      <div class="participant-video__name" :class="{ mine: isMe }">
        <div class="participant-video__name-text">
          <span>
            {{ participant.nickname }}
          </span>
        </div>
        <popover
          v-if="!isMe"
          trigger="click"
          placement="right-end"
          popperClass="participant-video__menu"
          :width="120"
          @visible="visible"
        >
          <button
            slot="reference"
            class="participant-video__setting"
            :class="{ hover: hover, active: btnActive }"
          >
            {{ $t('common.menu') }}
          </button>

          <ul class="video-popover">
            <li>
              <button class="video-pop__button" @click="mute">
                {{
                  participant.mute
                    ? $t('service.participant_mute_cancel')
                    : $t('service.participant_mute')
                }}
              </button>
            </li>
            <li v-if="iamLeader">
              <button class="video-pop__button" @click="kickout">
                {{ $t('service.participant_kick') }}
              </button>
            </li>
          </ul>
        </popover>
      </div>
    </div>
  </article>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import { CAMERA } from 'configs/device.config'
import { proxyUrl } from 'utils/file'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import touchMixin from 'mixins/touch'

import PanoVideo from 'PanoVideo'

import Profile from 'Profile'
import Popover from 'Popover'

export default {
  name: 'ParticipantVideo',
  mixins: [confirmMixin, toastMixin, touchMixin],
  components: {
    Profile,
    Popover,
    PanoVideo,
  },
  data() {
    return {
      inited: false,
      hover: false,
      btnActive: false,
      statusHover: {},
      touched: null,
    }
  },
  props: {
    participant: Object,
  },
  computed: {
    ...mapGetters([
      'mainView',
      'speaker',
      'roomInfo',
      'viewForce',
      'view',
      'initing',
      'restrictedRoom',
    ]),
    profileUrl() {
      if (!this.participant.path) {
        return ''
      } else {
        return proxyUrl(this.participant.path)
      }
    },
    showProfile() {
      if (!this.participant.hasVideo) {
        return true
      }
      if (this.participant.hasVideo && !this.participant.video) {
        return true
      }
      return false
    },
    isMe() {
      return this.participant.id === this.account.uuid
    },
    isCurrent() {
      return this.mainView.id === this.participant.id
    },
    isLeader() {
      return this.participant.roleType === ROLE.LEADER
    },
    iamLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    cameraStatus() {
      if (this.participant.hasVideo) {
        if (this.participant.cameraStatus === CAMERA.CAMERA_OFF) {
          return 'off'
        } else if (this.participant.cameraStatus === CAMERA.APP_IS_BACKGROUND) {
          return 'background'
        }
        return 'on'
      } else {
        return -1
      }
    },
    isMuted() {
      if (this.isMe || this.speaker.isOn === false) {
        return 'muted'
      }
      return false
    },
  },
  watch: {
    'participant.nickname': 'participantInited',
    participant() {},
    cameraStatus(status, oldStatus) {
      if (status === -1 || oldStatus === -1) return
      if (status === oldStatus) return
      if (status === 'off') {
        if (oldStatus === 'background') return
        this.addChat({
          name: this.participant.nickname,
          status: 'stream-stop',
          type: 'system',
        })
      } else if (status === 'background') {
        this.addChat({
          name: this.participant.nickname,
          status: 'stream-background',
          type: 'system',
        })
      } else if (status === 'on') {
        this.addChat({
          name: this.participant.nickname,
          status: 'stream-start',
          type: 'system',
        })
      }
    },
  },
  methods: {
    ...mapActions(['setMainView', 'addChat']),
    participantInited(name, oldName) {
      if (this.participant.me || this.initing === true) return
      if (name !== oldName && name.length > 0 && this.inited === false) {
        this.inited = true
        this.toastDefault(
          this.$t('service.chat_invite', { name: this.participant.nickname }),
        )
        const chatObj = {
          name: name,
          status: 'invite',
          type: 'system',
        }
        this.addChat(chatObj)
      }
    },
    hoverContents() {
      const status = this.$el.querySelector('.participant-video__network')
      if (!status) return
      const statusTooltip = this.$el.querySelector(
        '.participant-video__network-hover',
      )
      if (statusTooltip) {
        //this.$root.$el.append(this.$refs['popover'])
        document.body.append(statusTooltip)
      }
      const top = window.pageYOffset + status.getBoundingClientRect().top
      const left = window.pageXOffset + status.getBoundingClientRect().left

      this.$set(this.statusHover, 'top', top + 'px')
      this.$set(this.statusHover, 'left', left + 'px')
      this.hover = true
    },
    leaveContents() {
      this.hover = false
    },
    visible(val) {
      this.btnActive = val
    },
    doEvent() {
      this.changeMain()
    },
    changeMain() {
      if (this.restrictedRoom && this.account.roleType !== ROLE.LEADER) return
      if (!this.participant.hasCamera) {
        this.toastDefault(this.$t('service.participant_no_stream'))
        return
      }
      if (this.account.roleType === ROLE.LEADER) {
        if (this.view === VIEW.AR) {
          if (this.participant.hasArFeature === false) {
            this.toastDefault(this.$t('service.chat_ar_unsupport'))
            return
          }
          this.toastDefault(this.$t('service.participant_ar_cannot_change'))
          return
          // this.confirmCancel(
          //  this.$t('service.participant_ar_change_alarm'),
          //   { text: this.$t('button.confirm'), action: this.changeAr },
          //   {
          //     text: this.$t('button.cancel'),
          //   },
          // )
          // return
        }
        this.$emit('selectMain')
        // this.confirmCancel(
        //   this.$t('service.participant_sharing'),
        //   { text: this.$t('button.stream_sharing'), action: this.forceMain },
        //   {
        //     text: this.$t('button.stream_normal'),
        //     action: this.normalMain,
        //     backdrop: true,
        //   },
        // )
      } else {
        if (this.view === VIEW.AR) {
          this.toastDefault(this.$t('service.participant_ar_cannot_change'))
          return
        }
        if (this.viewForce === true) {
          this.toastDefault(
            this.$t('service.participant_sharing_cannot_change'),
          )
          return
        }
        this.setMainView({ id: this.participant.id })
      }
    },
    changeAr() {
      // 참가자 ar 가능 여부 체크
      // 가능하면 퍼미션 체크
      // 퍼미션 허용이면 전체 사용자 메인뷰 변경
      // 신규 ar 공유 진행
      if (this.participant.permission === true) {
        // 메인뷰 변경

        this.$emit('selectMain')
      } else {
        // 퍼미션 요청
        this.$eventBus.$on('startAr', this.getPermission)
        this.$call.sendCapturePermission([this.participant.connectionId])
        // 리턴받는 퍼미션은 HeaderServiceLnb에서 처리
      }
    },
    getPermission(permission) {
      if (permission === true) {
        // this.forceMain()
        // this.$call.sendArFeatureStop()
        this.$nextTick(() => {
          this.$emit('selectMain')
          // this.$call.sendArFeatureStart(this.participant.id)
        })
      } else {
        this.toastDefault(this.$t('service.toast_refused_ar'))
        this.addChat({
          status: 'ar-deny',
          type: 'system',
        })
      }
      this.$nextTick(() => {
        this.$eventBus.$off('startAr', this.getPermission)
      })
    },
    profileImageError(event) {
      event.target.style.display = 'none'
    },
    mute() {
      const mute = this.participant.mute
      this.$call.mute(this.participant.connectionId, !mute)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
      })
    },
    kickout() {
      this.$eventBus.$emit('popover:close')
      this.serviceConfirmCancel(
        this.$t('service.participant_kick_confirm', {
          name: this.participant.nickname,
        }),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.$emit('kickout')
          },
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    requestVideo() {
      if (this.participant.cameraStatus === CAMERA.CAMREA_NONE) {
        this.toastDefault(this.$t('service.participant_no_stream'))
        return
      }
      this.$call.sendVideo(this.participant.id, true)
    },
  },
  beforeDestroy() {
    if (!this.participant.me && this.$call.session) {
      this.toastDefault(
        this.$t('service.chat_leave', { name: this.participant.nickname }),
      )
    }
  },
}
</script>
