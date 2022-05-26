<template>
  <transition name="header" mode="out-in">
    <workspace-header
      v-if="$route.name === 'workspace'"
      :logo="logo"
    ></workspace-header>
    <guest-header
      v-else-if="$route.name === 'connectioninfo'"
      :logo="logo"
    ></guest-header>
    <qr-header :logo="logo" v-else-if="$route.name === 'qr'"></qr-header>
    <service-header :logo="logo" v-else></service-header>
  </transition>
</template>

<script>
import WorkspaceHeader from './WorkspaceHeader'
import ServiceHeader from './ServiceHeader'
import GuestHeader from './GuestHeader'
import QrHeader from './QrHeader'

import { WHITE_LOGO } from 'configs/env.config'

const systemLogo = require('assets/image/logo_symtext.svg')

export default {
  name: 'Header',
  components: {
    WorkspaceHeader,
    ServiceHeader,
    GuestHeader,
    QrHeader,
  },
  data() {
    return {
      logo: systemLogo,
    }
  },
  computed: {},
  watch: {
    workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        if (WHITE_LOGO) {
          this.logo = WHITE_LOGO
        } else {
          this.logo = systemLogo
        }
      }
    },
  },
  methods: {
    updateHeaderLogo() {
      if (WHITE_LOGO) {
        this.logo = WHITE_LOGO
      } else {
        this.logo = systemLogo
      }
    },
  },
  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('update:settings', this.updateHeaderLogo)
    this.updateHeaderLogo()
  },
  beforeDestroy() {
    this.$eventBus.$off('update:settings', this.updateHeaderLogo)
  },
}
</script>
<style lang="scss" src="assets/style/header.scss"></style>

<style>
.header-enter-active {
  transition: 0.4s transform ease 0.2s;
}
.header-enter {
  transform: translateY(-80px);
}
.header-enter-to {
  transform: translateY(0);
}
</style>
