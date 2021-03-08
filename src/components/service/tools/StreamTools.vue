<template>
  <div class="stream-tools tools">
    <moving v-if="isLeader && mainView.streamMode"></moving>
    <pointing></pointing>
    <color></color>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
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
    ...mapGetters(['mainView']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
}
</script>
