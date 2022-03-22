<template>
  <header class="header">
    <div class="header-workspace">
      <img class="header-logo" :src="headerImgSrc" alt="logo" />
      <template v-if="hasLicense && workspace && workspace.uuid">
        <div class="header-divider"></div>
        <header-nav></header-nav>
      </template>
    </div>
  </header>
</template>

<script>
import { DEFAULT_LOGO } from 'configs/env.config'

export default {
  name: 'QRHeader',
  components: {},
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
