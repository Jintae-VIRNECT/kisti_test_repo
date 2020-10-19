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
      <template v-if="hasLicense && workspace && workspace.uuid">
        <div class="header-divider"></div>
        <header-nav></header-nav>
      </template>

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
import { SETTING_LOGO } from 'configs/env.config'
import HeaderNav from './partials/HeaderWorkspaceNav'
import HeaderTools from './partials/HeaderWorkspaceTools'
export default {
  name: 'WorkspaceHeader',
  components: {
    HeaderNav,
    HeaderTools,
  },
  data() {
    return {}
  },
  computed: {
    logo() {
      return SETTING_LOGO
    },
  },
  watch: {},
  methods: {
    logoError(event) {
      // event.target.style = 'border:solid 1px #fff;'
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
