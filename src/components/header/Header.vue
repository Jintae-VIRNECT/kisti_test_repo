<template>
  <div class="header">
    <div style="display:flex">
      <header-logo></header-logo>
      <div class="header-divider"></div>
      <header-nav></header-nav>
    </div>
    <header-tools></header-tools>
  </div>
</template>

<script>
import HeaderLogo from './partial/HeaderLogo'
import HeaderNav from './partial/HeaderNav'
import HeaderTools from './partial/HeaderTools'
export default {
  name: 'Header',
  components: {
    HeaderLogo,
    HeaderNav,
    HeaderTools,
  },
}
</script>
<style lang="scss" src="assets/style/header.scss"></style>
