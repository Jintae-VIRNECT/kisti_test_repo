<template>
  <article>
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      @click="changeMain"
    >
      <div class="participant-video__stream" v-if="session.stream">
        <video :srcObject="session.stream"></video>
      </div>
      <div class="participant-video__profile" v-else>
        <img
          class="participant-video__profile-background"
          :src="session.path"
          @error="profileImageError"
        />
        <profile
          :thumbStyle="{ width: '64px', height: '64px', margin: '10px auto 0' }"
          :image="session.path"
        ></profile>
      </div>
      <div
        v-if="!isMain"
        class="participant-video__status"
        :class="session.status"
      >
        <span>우수</span>
      </div>
      <!-- <img
        v-if="!isMain"
        class="participant-video__speaker"
        :src="
          session.audio
            ? require('assets/image/call/gnb_ic_voice_on.svg')
            : require('assets/image/call/gnb_ic_voice_off.svg')
        "
      /> -->
      <div class="participant-video__name">
        <span :class="{ active: isMain }">{{ session.nickName }}</span>
        <popover
          trigger="click"
          placement="right-end"
          popperClass="participant-video__menu"
          :width="120"
        >
          <button slot="reference" class="participant-video__setting">
            메뉴
          </button>

          <ul class="video-popover">
            <li>
              <button class="video-pop__button" @click="">
                음소거
              </button>
            </li>
            <li>
              <button class="video-pop__button" @click="">
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
import { mapGetters, mapActions } from 'vuex'
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
      onSpeaker: true,
    }
  },
  props: {
    session: Object,
  },
  computed: {
    ...mapGetters(['mainSession', 'speaker']),
    isMain() {
      if (this.session.uuid === 'main') {
        return true
      }
      return false
    },
    isCurrent() {
      if (this.mainSession.uuid === this.session.uuid) return true
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
    ...mapActions(['setMainSession']),
    changeMain() {
      this.setMainSession(this.session)
    },
    profileImageError(event) {
      event.target.style.display = 'none'
    },
    mute(e) {
      e.preventDefault()
      e.stopPropagation()

      this.onSpeaker = !this.onSpeaker
      // this.$call.audioOnOff(this.session.uuid, this.onSpeaker)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
