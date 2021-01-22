<template>
  <div class="header">
    <div class="header-wrapper offsetwidth">
      <header-logo></header-logo>
      <header-nav></header-nav>
      <header-tools></header-tools>
    </div>
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
