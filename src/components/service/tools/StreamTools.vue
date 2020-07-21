<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color v-if="isLeader"></color>
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
      active: 'pointing',
      isRecording: false,
    }
  },
  computed: {
    isLeader() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
