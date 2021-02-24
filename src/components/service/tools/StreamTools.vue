<template>
  <div class="stream-tools tools">
    <moving v-if="isLeader"></moving>
    <pointing></pointing>
    <color></color>
  </div>
</template>

<script>
import { Moving, Pointing, Color } from './partials'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'StreamTools',
  components: {
    Moving,
    Pointing,
    Color,
  },
  computed: {
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
}
</script>
