<template>
  <article @mouseenter="hoverContents" @mouseleave="leaveContents">
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      @dblclick="changeMain"
    >
      <!-- <div class="participant-video__stream" v-if="participant.video">
        <video
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
        ></video>
      </div> -->
      <div class="participant-video__profile">
        <audio
          v-if="!participant.video && !participant.me"
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
        ></audio>
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
        <!--
          TODO: beta check
        <div class="participant-video__network" :class="participant.status">
          <div
            class="participant-video__network-hover"
            :class="{ hover: hover }"
            :style="statusHover"
          >
            <span :class="participant.status"
              >신호 세기 : {{ participant.status | networkStatus }}</span
            >
          </div>
        </div>
        -->
        <span class="participant-video__leader" v-if="isLeader">
          Leader
        </span>
      </div>
      <div class="participant-video__device" v-if="!isMe">
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
            메뉴
          </button>

          <ul class="video-popover">
            <li>
              <button class="video-pop__button" @click="mute">
                음소거
              </button>
            </li>
            <li v-if="isLeader">
              <button
                class="video-pop__button"
                @click="disconnectUser(account.nickname)"
              >
                내보내기
              </button>
            </li>
          </ul>
        </popover>
      </div>
    </div>
  </article>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import { ROLE } from 'configs/remote.config'
import Profile from 'Profile'
import Popover from 'Popover'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'ParticipantVideo',
  mixins: [confirmMixin],
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
    ...mapGetters(['mainView', 'speaker']),
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
      if (this.participant.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    speaker(val) {
      if (this.$el.querySelector('video')) {
        this.$el.querySelector('video').muted = val
      }
      if (this.$el.querySelector('audio')) {
        this.$el.querySelector('audio').muted = val
      }
    },
  },
  methods: {
    ...mapMutations(['setMainView']),
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
      if (!this.participant.video) return
      this.setMainView(this.participant.id)
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
    disconnectUser(nickName) {
      this.confirmCancel(
        `${nickName}님을 협업에서 제외 하시겠습니까?`,
        {
          text: '확인',
          action: this.kick,
        },
        {
          text: '취소',
        },
      )
    },
    kick() {
      this.$call.disconnect(this.participant.connectionId)
    },
  },
}
</script>
