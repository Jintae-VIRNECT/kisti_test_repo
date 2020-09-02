<template>
  <article @mouseenter="hoverContents" @mouseleave="leaveContents">
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      @dblclick="changeMain"
    >
      <div class="participant-video__stream" v-if="participant.hasVideo">
        <video
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
          :muted="isMe"
        ></video>
      </div>
      <audio
        v-else-if="!participant.me"
        :srcObject.prop="participant.stream"
        autoplay
        playsinline
        loop
        :muted="isMe || mainView.id === participant.id"
      ></audio>
      <div class="participant-video__profile" v-if="showProfile">
        <img
          v-if="participant.path && participant.path !== 'default'"
          class="participant-video__profile-background"
          :src="participant.path"
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
          trigger="click"
          placement="right-end"
          popperClass="participant-video__menu"
          :width="120"
          @visible="visible"
        >
          <button
            slot="reference"
            v-if="!isMe"
            class="participant-video__setting"
            :class="{ hover: hover, active: btnActive }"
          >
            {{ $t('common.menu') }}
          </button>

          <ul class="video-popover">
            <li>
              <button class="video-pop__button" @click="mute">
                {{ $t('service.participant_mute') }}
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
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'

import Profile from 'Profile'
import Popover from 'Popover'

export default {
  name: 'ParticipantVideo',
  mixins: [confirmMixin, toastMixin],
  components: {
    Profile,
    Popover,
  },
  data() {
    return {
      hover: false,
      btnActive: false,
      statusHover: {},
    }
  },
  props: {
    participant: Object,
  },
  computed: {
    ...mapGetters(['mainView', 'speaker', 'roomInfo', 'viewForce', 'view']),
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
      if (this.participant.id === this.account.uuid) {
        return true
      }
      return false
    },
    isCurrent() {
      if (this.mainView.id === this.participant.id) return true
      return false
    },
    isLeader() {
      if (this.participant.roleType === ROLE.LEADER) {
        return true
      } else {
        return false
      }
    },
    iamLeader() {
      if (this.account.roleType === ROLE.LEADER) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    speaker(val) {
      if (this.isMe) return
      if (this.$el.querySelector('video')) {
        this.$el.querySelector('video').muted = val
      }
      if (this.$el.querySelector('audio')) {
        this.$el.querySelector('audio').muted = val
      }
    },
    participant() {},
  },
  methods: {
    ...mapActions(['setMainView', 'addChat']),
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
    changeMain() {
      if (!this.participant.hasVideo) return
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

        this.forceMain()
      } else {
        // 퍼미션 요청
        this.$eventBus.$on('startAr', this.getPermission)
        this.$call.permission([this.participant.connectionId])
        // 리턴받는 퍼미션은 HeaderServiceLnb에서 처리
      }
    },
    getPermission(permission) {
      if (permission === true) {
        // this.forceMain()
        // this.$call.stopArFeature()
        this.$nextTick(() => {
          this.forceMain()
          // this.$call.startArFeature(this.participant.id)
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
  },
}
</script>
