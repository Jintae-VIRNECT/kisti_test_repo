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

      <p class="header-workspace-select">
        {{ $t('workspace.workspace_select') }}
      </p>

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
import { WHITE_LOGO, DEFAULT_LOGO } from 'configs/env.config'
import HeaderNav from './partials/HeaderWorkspaceNav'
import HeaderTools from './partials/HeaderWorkspaceTools'
export default {
  name: 'WorkspaceHeader',
  components: {
    HeaderNav,
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
