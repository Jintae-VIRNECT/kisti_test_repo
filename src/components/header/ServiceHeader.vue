<template>
  <header class="header service">
    <div v-if="!isMobileSize" class="header-service">
      <img
        v-if="logo !== false"
        @error="logoError"
        class="header-logo"
        :src="logo"
      />
      <img v-else class="header-logo" src="~assets/image/logo_symtext.svg" />
      <header-lnb></header-lnb>

      <header-tools></header-tools>
    </div>
    <service-mobile-header v-else></service-mobile-header>
  </header>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
import HeaderLnb from './partials/HeaderServiceLnb'
import HeaderTools from './partials/HeaderServiceTools'
import ServiceMobileHeader from './ServiceMobileHeader'

export default {
  name: 'ServiceHeader',
  components: {
    HeaderLnb,
    HeaderTools,
    ServiceMobileHeader,
  },
  data() {
    return {
      logo: WHITE_LOGO,
    }
  },
  methods: {
    logoError() {
      this.logo = DEFAULT_LOGO
    },
  },
}
</script>
