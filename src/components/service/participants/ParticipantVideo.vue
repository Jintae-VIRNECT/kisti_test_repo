<template>
  <article>
    <div
      class="participant-video"
      :class="{ current: isCurrent }"
      :id="'video-view__' + session.nodeId"
      @click="changeMain"
    >
      <div class="participant-video__profile">
        <img :src="session.path" @error="profileImageError" />
        <profile
          :thumbStyle="{ width: '64px', height: '64px', margin: '10px auto 0' }"
          :src="session.path"
        ></profile>
      </div>
      <img
        v-if="!isMain"
        class="participant-video__speaker"
        :src="
          session.audio
            ? require('assets/image/call/gnb_ic_voice_on.svg')
            : require('assets/image/call/gnb_ic_voice_off.svg')
        "
      />

      <div v-if="!isMain" class="participant-video__status good">
        <span>우수</span>
      </div>
      <div class="participant-video__name">
        <span :class="{ active: isMain }">{{ session.nickName }}</span>
      </div>
    </div>
  </article>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import Profile from 'Profile'

export default {
  name: 'ParticipantVideo',
  components: {
    Profile,
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
      if (this.session.nodeId === 'main') {
        return true
      }
      return false
    },
    isCurrent() {
      if (this.mainSession.nodeId === this.session.nodeId) return true
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
      // this.$openvidu.audioOnOff(this.session.nodeId, this.onSpeaker)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
