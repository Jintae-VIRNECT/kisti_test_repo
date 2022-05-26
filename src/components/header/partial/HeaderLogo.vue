<template>
  <img class="header-logo" :src="headerImgSrc" alt="logo" />
</template>

<script>
import { proxyUrl } from 'utils/file'
import { DEFAULT_LOGO } from 'configs/env.config'
export default {
  name: 'HeaderLogo',
  props: {
    logo: {
      type: [String, Object],
    },
  },
  data() {
    return {
      headerImgSrc: null,
    }
  },
  watch: {
    logo(val, oldVal) {
      if (val && val !== oldVal) {
        this.headerImgSrc = val
      }
    },
  },
  methods: {
    logoError() {
      this.logo = proxyUrl(DEFAULT_LOGO)
    },
  },
  mounted() {
    this.headerImgSrc = this.logo
  },
}
</script>
