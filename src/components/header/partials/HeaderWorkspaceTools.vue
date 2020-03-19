<template>
  <ul class="header-workspace-tools">
    <mic></mic>

    <speaker></speaker>

    <notice></notice>
    <span>프로필</span>
  </ul>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Notice from '../tools/Notice'

export default {
  name: 'HeaderTools',
  components: {
    Mic,
    Speaker,
    Notice,
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
          let state = this.$openvidu.getState()

          this.callMic(state.audio)
          this.callStream(state.video)
        }
      },
    },
  },
  methods: {
    ...mapActions(['callMic', 'callStream']),
    leave() {
      this.$openvidu.leave()
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
