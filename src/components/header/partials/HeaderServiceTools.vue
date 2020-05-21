<template>
  <div class="header-tools">
    <!-- <stream></stream> -->

    <mic></mic>

    <speaker></speaker>

    <notice></notice>

    <call-time></call-time>

    <button class="header-tools__leave" @click="leave">나가기</button>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

// import Stream from '../tools/Stream'
import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Notice from '../tools/Notice'
import CallTime from '../tools/CallTime'

export default {
  name: 'HeaderTools',
  components: {
    // Stream,
    Mic,
    Speaker,
    Notice,
    CallTime,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['mainSession', 'mute']),
  },
  watch: {
    mainSession: {
      deep: true,
      handler: function(val) {
        if (val.stream) {
          // let state = this.$call.getState()
          // this.callMic(state.audio)
          // this.callStream(state.video)
        }
      },
    },
  },
  methods: {
    ...mapActions(['callMic', 'callStream']),
    leave() {
      this.$call.leave()

      this.$router.push({ name: 'workspace' })
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
