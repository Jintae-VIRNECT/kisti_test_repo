<template>
  <div class="main-body ar">
    <ar-tools v-show="isArToolsVisible"></ar-tools>
    <menus v-if="!isMobileSize"></menus>
    <ar-view></ar-view>
  </div>
</template>

<script>
import ArTools from './tools/ArTools'
import Menus from './tools/Menus'
import ArView from './ar/ArView'

import { mapGetters } from 'vuex'

export default {
  name: 'ServiceAr',
  components: {
    ArTools,
    Menus,
    ArView,
  },
  computed: {
    ...mapGetters(['isControlPopOverVisible', 'controlActive']),
    isArToolsVisible() {
      return !this.isMobileSize || (this.isMobileSize && !this.is3dControlling)
    },
    is3dControlling() {
      return this.isControlPopOverVisible && this.controlActive
    },
  },
}
</script>
