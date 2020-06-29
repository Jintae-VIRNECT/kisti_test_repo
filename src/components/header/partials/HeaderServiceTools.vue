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
import { DRAWING, AR_DRAWING, ROLE } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'

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
    ...mapGetters(['mainView', 'mute', 'view']),
  },
  watch: {
    mainView: {
      deep: true,
      handler: function(val) {
        if (val && val.stream) {
          let state = this.$call.getState()
          this.callMic(state.audio)
        }
      },
    },
  },
  methods: {
    ...mapActions(['callMic']),
    leave() {
      try {
        if (this.account.roleType === ROLE.EXPERT_LEADER) {
          if (this.view === VIEW.DRAWING) {
            this.$call.drawing(DRAWING.END_DRAWING)
          }
          if (this.view === VIEW.AR) {
            this.$call.arDrawing(AR_DRAWING.END_DRAWING)
          }
        }
        this.$nextTick(() => {
          this.$call.leave()
          this.$router.push({ name: 'workspace' })
        })
      } catch (err) {
        this.$router.push({ name: 'workspace' })
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
