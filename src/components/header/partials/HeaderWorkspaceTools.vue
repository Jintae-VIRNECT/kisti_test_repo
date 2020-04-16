<template>
  <ul class="header-workspace-tools">
    <mic></mic>
    <speaker></speaker>
    <notice></notice>
    <profile></profile>
  </ul>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Notice from '../tools/Notice'
import Profile from '../tools/HeaderProfile'

export default {
  name: 'HeaderTools',
  components: {
    Mic,
    Speaker,
    Notice,
    Profile,
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
