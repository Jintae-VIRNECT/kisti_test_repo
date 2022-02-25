<template>
  <div class="header">
    <div class="header-wrapper offsetwidth">
      <header-logo :logo="logo"></header-logo>
      <header-nav></header-nav>
      <header-tools></header-tools>
    </div>
  </div>
</template>

<script>
import HeaderLogo from './partial/HeaderLogo'
import HeaderNav from './partial/HeaderNav'
import HeaderTools from './partial/HeaderTools'

import { proxyUrl } from 'utils/file'
import { WHITE_LOGO } from 'configs/env.config'

const systemLogo = require('assets/image/logo_symtext.svg')

export default {
  name: 'Header',
  components: {
    HeaderLogo,
    HeaderNav,
    HeaderTools,
  },
  data() {
    return {
      logo: systemLogo,
    }
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        if (WHITE_LOGO) {
          this.logo = proxyUrl(WHITE_LOGO)
        } else {
          this.logo = systemLogo
        }
      }
    },
  },
  methods: {
    updateHeaderLogo() {
      if (WHITE_LOGO) {
        this.logo = proxyUrl(WHITE_LOGO)
      } else {
        this.logo = systemLogo
      }
    },
  },
  mounted() {
    this.$eventBus.$on('update:settings', this.updateHeaderLogo)
    this.updateHeaderLogo()
  },
  beforeDestroy() {
    this.$eventBus.$off('update:settings', this.updateHeaderLogo)
  },
}
</script>
