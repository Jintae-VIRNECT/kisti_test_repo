<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color></color>
    <template v-if="!isTablet && !isSafari">
      <div class="division"></div>
      <screen-share></screen-share>
    </template>
    <position-map v-if="isOnpremise"></position-map>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'
import { Pointing, Color, ScreenShare, PositionMap } from './partials'

export default {
  name: 'StreamTools',
  components: {
    Pointing,
    Color,
    ScreenShare,
    PositionMap,
  },
  computed: {
    ...mapGetters(['mainView']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
}
</script>
