<template>
  <div class="header-tools">
    <chat v-if="!isScreenDesktop"></chat>

    <stream v-if="hasVideo"></stream>

    <mic v-if="hasAudio"></mic>

    <speaker></speaker>

    <notice></notice>

    <call-time></call-time>

    <button class="header-tools__leave" @click.once="leave">
      {{ $t('button.leave') }}
    </button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import Stream from '../tools/Stream'
import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Notice from '../tools/Notice'
import CallTime from '../tools/CallTime'
import Chat from '../tools/Chat'

export default {
  name: 'HeaderTools',
  components: {
    Stream,
    Mic,
    Speaker,
    Notice,
    CallTime,
    Chat,
  },
  data() {
    return {
      hideStream: false,
    }
  },
  computed: {
    ...mapGetters(['myInfo', 'screenSharing']),
    hasVideo() {
      if (this.$route.name === 'workspace') {
        return true
      }
      return !!this.myInfo.hasCamera
    },
    hasAudio() {
      if (this.$route.name === 'workspace') {
        return true
      }
      return !!this.myInfo.hasAudio
    },
  },
  methods: {
    leave() {
      try {
        this.$call.leave()
        this.$router.push({ name: 'workspace' })
      } catch (err) {
        this.$router.push({ name: 'workspace' })
      }
    },
    /**
     * @TODO store 체크로 수정 예정
     *
     * 현재 내 카메라가 없으나 화면 공유를 위해
     * 활성화 되는 카메라를 숨기기 위한 코드
     * @param {Boolean} flag 플래그값
     */
    toggleStream(flag) {
      this.hideStream = flag
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('call:logout', this.leave)
  },
  beforeDestroy() {
    this.$eventBus.$off('call:logout', this.leave)
  },
}
</script>
