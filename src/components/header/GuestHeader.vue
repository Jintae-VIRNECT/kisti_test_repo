<template>
  <header class="header">
    <div class="header-workspace">
      <img class="header-logo" :src="headerImgSrc" alt="logo" />

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
import { DEFAULT_LOGO } from 'configs/env.config'
import HeaderTools from './partials/HeaderGuestTools'
export default {
  name: 'GuestHeader',
  components: {
    HeaderTools,
  },
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
  computed: {},
  watch: {
    logo(val, oldVal) {
      if (val && val !== oldVal) {
        this.headerImgSrc = val
      }
    },
  },
  methods: {
    logoError() {
      this.headerImgSrc = DEFAULT_LOGO
    },
  },

  /* Lifecycles */
  mounted() {
    this.headerImgSrc = this.logo
  },
}
</script>
