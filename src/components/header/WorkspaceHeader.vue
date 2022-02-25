<template>
  <header
    class="header"
    :class="{ 'workspace-selected': workspace && workspace.uuid }"
  >
    <div class="header-workspace">
      <img class="header-logo" :src="headerImgSrc" alt="logo" />
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
import { DEFAULT_LOGO } from 'configs/env.config'
import HeaderNav from './partials/HeaderWorkspaceNav'
import HeaderTools from './partials/HeaderWorkspaceTools'
export default {
  name: 'WorkspaceHeader',
  components: {
    HeaderNav,
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
  watch: {
    logo(val, oldVal) {
      if (val && val !== oldVal) {
        this.headerImgSrc = val
      }
    },
  },
  computed: {},
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
