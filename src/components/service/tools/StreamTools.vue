<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color v-if="account.roleType === EXPERT_LEADER"></color>
  </div>
</template>

<script>
import { Pointing, Color } from './partials'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'StreamTools',
  components: {
    Pointing,
    Color,
  },
  data() {
    return {
      EXPERT_LEADER: ROLE.EXPERT_LEADER,
      active: 'pointing',
      isRecording: false,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
