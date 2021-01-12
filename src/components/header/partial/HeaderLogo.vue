<template>
  <img
    v-if="logo !== false"
    @error="logoError"
    class="header-logo"
    :src="logo"
  />
  <img v-else class="header-logo" src="~assets/image/logo_symtext.svg" />
</template>

<script>
import { proxyUrl } from 'utils/file'
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
export default {
  name: 'HeaderLogo',
  data() {
    return {
      logo: proxyUrl(WHITE_LOGO),
    }
  },
  methods: {
    logoError() {
      this.logo = proxyUrl(DEFAULT_LOGO)
    },
  },
}
</script>
