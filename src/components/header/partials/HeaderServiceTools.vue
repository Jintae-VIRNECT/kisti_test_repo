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
    return {}
  },
  computed: {
    ...mapGetters(['mainView', 'mute', 'view', 'participants']),
    hasVideo() {
      if (this.$route.name === 'workspace') {
        return true
      }
      if (this.participants.length > 0 && this.participants[0].hasVideo) {
        return true
      } else {
        return false
      }
    },
    hasAudio() {
      if (this.$route.name === 'workspace') {
        return true
      }
      if (this.participants.length > 0 && this.participants[0].hasAudio) {
        return true
      } else {
        return false
      }
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
