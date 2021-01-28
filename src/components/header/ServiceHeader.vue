<template>
  <header class="header">
    <div class="header-service">
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
  </header>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
import HeaderLnb from './partials/HeaderServiceLnb'
import HeaderTools from './partials/HeaderServiceTools'
export default {
  name: 'ServiceHeader',
  components: {
    HeaderLnb,
    HeaderTools,
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
