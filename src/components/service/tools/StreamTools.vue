<template>
  <div class="stream-tools tools">
    <moving v-if="isLeader && mainView.streamMode"></moving>
    <pointing></pointing>
    <color></color>
    <template v-if="!isTablet && !isSafari">
      <div class="division"></div>
      <screen-share></screen-share>
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { Pointing, Color, ScreenShare, Moving } from './partials'

export default {
  name: 'StreamTools',
  components: {
    Moving,
    Pointing,
    Color,
    ScreenShare,
  },
  computed: {
    ...mapGetters(['mainView']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
}
</script>
