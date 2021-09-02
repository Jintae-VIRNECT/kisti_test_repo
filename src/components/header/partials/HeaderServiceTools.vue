<template>
  <div class="header-tools">
    <spot-control
      v-if="isLeader && isOnpremise && isSpotControlActive"
    ></spot-control>

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
import SpotControl from '../tools/SpotControl'
import { ROLE } from 'configs/remote.config'
import { SPOT_CONTROL_ACTIVE } from 'configs/env.config'

export default {
  name: 'HeaderTools',
  components: {
    Stream,
    Mic,
    Speaker,
    Notice,
    CallTime,
    Chat,
    SpotControl,
  },
  data() {
    return {
      hideStream: false,
      isSpotControlActive: SPOT_CONTROL_ACTIVE, //spot control 활성화 여부 (from config서버)
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
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    leave() {
      try {
        this.$call.leave()

        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace' })
        }
      } catch (err) {
        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace' })
        }
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
