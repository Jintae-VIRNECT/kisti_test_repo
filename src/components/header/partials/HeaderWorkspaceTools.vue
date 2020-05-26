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
    ...mapGetters(['mainView', 'mute']),
  },
  watch: {
    mainView: {
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
  },

  /* Lifecycles */
  mounted() {},
}
</script>
