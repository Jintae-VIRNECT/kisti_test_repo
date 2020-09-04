<template>
  <div class="dashboard--wrapper">
    <top-header></top-header>
    <side-bar></side-bar>
    <router-view></router-view>
  </div>
</template>

<script>
import TopHeader from 'components/header/TopHeader'

import SideBar from 'components/sidebar/SideBar'
export default {
  name: 'DashBoardLayout',
  components: {
    TopHeader,
    SideBar,
  },
}
</script>

<style lang="scss">
@import '~assets/style/layout.scss';
@import '~assets/style/dashboard.scss';
.dashboard--wrapper {
  position: relative;
  width: 100%;

  height: 130vh;
  background: #1e1e2f;
}
</style>
