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
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
