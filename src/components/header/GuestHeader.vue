<template>
  <header class="header">
    <div class="header-workspace">
      <img
        v-if="logo !== false"
        @error="logoError"
        class="header-logo"
        :src="logo"
      />
      <img v-else class="header-logo" src="~assets/image/logo_symtext.svg" />

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
import HeaderTools from './partials/HeaderGuestTools'
export default {
  name: 'GuestHeader',
  components: {
    HeaderTools,
  },
  data() {
    return {
      logo: WHITE_LOGO,
    }
  },
  computed: {},
  watch: {},
  methods: {
    logoError() {
      this.logo = DEFAULT_LOGO
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
