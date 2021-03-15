<template>
  <div class="stream-tools tools">
    <moving v-if="isLeader && isFITT360"></moving>
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
import { DEVICE } from 'configs/device.config'

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
    isFITT360() {
      return this.mainView.deviceType === DEVICE.FITT360
    },
  },
}
</script>
