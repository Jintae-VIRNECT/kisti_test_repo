<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color v-if="myRole === EXPERT_LEADER"></color>
  </div>
</template>

<script>
import { Pointing, Color } from './partials'
import { mapGetters } from 'vuex'

export default {
  name: 'StreamTools',
  components: {
    Pointing,
    Color,
  },
  data() {
    return {
      active: 'pointing',
      isRecording: false,
    }
  },
  computed: {
    ...mapGetters(['myRole']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
