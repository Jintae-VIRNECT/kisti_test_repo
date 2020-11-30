<template>
  <div class="header-logo">
    <img
      v-if="logo !== false"
      @error="logoError"
      class="header-logo--image"
      :src="logo"
    />
    <img
      v-else
      class="header-logo--image"
      src="~assets/image/logo_symtext.svg"
    />
    <div class="header-logo--text"></div>
  </div>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
export default {
  name: 'HeaderLogo',
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

<style lang="scss">
.header-logo {
  display: flex;
  padding: 1.1429rem 0rem;
}

.header-logo--image {
  max-width: 100%;
  height: 2.2286rem;
  margin-right: 0.8571rem;
  border-radius: 0rem;
}

.header-logo--text {
  width: 7.5714rem;
  height: 1.8571rem;
  margin-top: 0.2143rem;
  font-weight: 500;
  font-size: 1.0286rem;
  text-align: center;
  background: #1f7ffa url('~assets/image/logo_sub_text.svg');
  border-radius: 0.1714rem;
}
</style>
