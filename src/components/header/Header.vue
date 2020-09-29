<template>
  <div class="header">
    <header-logo></header-logo>
    <header-workspace-selector></header-workspace-selector>
    <header-tools></header-tools>
  </div>
</template>

<script>
import HeaderLogo from './partial/HeaderLogo'
import HeaderWorkspaceSelector from './partial/HeaderWorkspaceSelector'
import HeaderTools from './partial/HeaderTools'
export default {
  name: 'Header',
  components: {
    HeaderLogo,
    HeaderWorkspaceSelector,
    HeaderTools,
  },
}
</script>

<style lang="scss">
.header {
  position: absolute;
  width: 100%;
  height: 68px;
  background: #252525;
}
</style>
