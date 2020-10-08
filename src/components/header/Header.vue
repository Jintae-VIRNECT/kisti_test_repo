<template>
  <div class="header">
    <header-logo></header-logo>
    <div class="header-divider"></div>
    <header-nav></header-nav>
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

<style lang="scss">
.header {
  position: absolute;
  display: flex;
  // flex-direction: row;
  // justify-content: space-between;
  width: 100%;
  height: 68px;
  padding: 0px 320px;
  background: #252525;
}

.header-divider {
  width: 1px;
  height: 1.571rem;
  margin: auto 30px;
  background-color: rgba(#d8d8d8, 0.2);
}
</style>
