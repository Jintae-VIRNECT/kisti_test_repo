<template>
  <article @mouseenter="hover = true" @mouseleave="hover = false">
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      @dblclick="changeMain"
    >
      <div class="participant-video__stream" v-if="participant.video">
        <video
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
        ></video>
      </div>
      <div class="participant-video__profile" v-else>
        <audio
          :srcObject.prop="participant.stream"
          autoplay
          playsinline
          loop
        ></audio>
        <img
          class="participant-video__profile-background"
          :src="participant.path ? participant.path : 'default'"
          @error="onImageError"
        />
        <div class="participant-video__profile-dim"></div>
        <profile
          :thumbStyle="{ width: '64px', height: '64px', margin: '10px auto 0' }"
          :image="participant.path"
        ></profile>
      </div>
      <div class="participant-video__mute" v-if="participant.mute"></div>
      <div
        class="participant-video__status"
        :class="[participant.status, { hover: hover }]"
      >
        <div class="participant-video__status-hover">
          <span :class="participant.status">{{
            participant.status | networkStatus
          }}</span>
        </div>
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
      <div class="participant-video__name">
        <p :class="{ mine: isMe }" class="participant-video__name-text">
          {{ participant.nickname }}
        </p>
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
            <li v-if="myRole === EXPERT_LEADER">
              <button class="video-pop__button" @click="disconnectUser">
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
import Profile from 'Profile'
import Popover from 'Popover'

export default {
  name: 'ParticipantVideo',
  components: {
    Profile,
    Popover,
  },
  data() {
    return {
      hover: false,
      btnActive: false,
    }
  },
  props: {
    participant: Object,
  },
  computed: {
    ...mapGetters(['myRole', 'mainView', 'speaker']),
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
  },
  watch: {
    speaker(val) {
      if (this.$el.querySelector('video')) {
        this.$el.querySelector('video').muted = val
      }
    },
  },
  methods: {
    ...mapMutations(['setMainView']),
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
    disconnectUser() {
      this.$call.disconnect(this.participant.connectionId)
    },
  },

  /* Lifecycles */
  mounted() {
    console.log(this.myRole)
  },
}
</script>
