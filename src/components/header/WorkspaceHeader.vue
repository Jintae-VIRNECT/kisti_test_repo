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

      <!-- 워크스페이스 선택 문구 : 모바일에서만 표시-->
      <h1 v-if="!workspace.uuid" class="header-workspace-select-title">
        {{ $t('workspace.workspace_select') }}
      </h1>

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
