<template>
  <div class="stream-tools tools">
    <template v-if="isMobileSize">
      <div class="mobile-tools-container stream" :class="{ active }">
        <color :disableTooltip="true"></color>
        <pointing :disableTooltip="true"></pointing>
      </div>
      <button class="tools-toggle-btn" :class="{ active }" @click="toggle">
        <img src="~assets/image/call/icn_dropdown_new.svg" alt="dropdown" />
      </button>
    </template>
    <template v-else>
      <pointing></pointing>
      <color></color>
      <template v-if="!isTablet && !isSafari && !isMobileSize">
        <div class="division"></div>
        <screen-share></screen-share>
      </template>
      <!-- PC에서만 지원 -->
      <position-map v-if="isOnpremise && !isMobileSize"></position-map>
    </template>
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
  data() {
    return {
      active: false,
    }
  },
  computed: {
    ...mapGetters(['mainView']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    toggle() {
      this.active = !this.active
    },
  },
}
</script>
