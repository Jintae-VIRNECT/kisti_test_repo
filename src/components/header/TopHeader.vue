<template>
  <nav class="top-header">
    <!-- <img class="header-logo" src="~assets/img/logo_symtext.svg" /> -->
    <div class="header-profile">DUMMY USER</div>
  </nav>
</template>

<script>
export default {
  name: 'TopHeader',
}
</script>

<style lang="scss">
.top-header {
  position: fixed;
  z-index: 4;
  display: flex;
  width: 100%;
  height: 78px;
  background: #1e1e2f;
}
.header-logo {
  max-width: 160px;
  height: 30.002px;
  margin: 18px 20.996px;
}
.header-profile {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 60px;
  margin-left: auto;
}
</style>
