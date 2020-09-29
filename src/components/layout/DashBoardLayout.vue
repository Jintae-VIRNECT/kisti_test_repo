<template>
  <div class="dashboard--wrapper">
    <dash-board-header></dash-board-header>
    <div class="content">content</div>
    <dash-board-footer></dash-board-footer>
  </div>
</template>

<script>
import DashBoardHeader from 'components/header/Header'
import DashBoardFooter from 'components/footer/Footer'
export default {
  name: 'DashBoardLayout',
  components: {
    DashBoardHeader,
    DashBoardFooter,
  },
}
</script>

<style lang="scss">
@import '~assets/style/layout.scss';
@import '~assets/style/dashboard.scss';
.dashboard--wrapper {
  position: relative;
  width: 100%;
  // height: 100%;
  height: 100vh;
}

.content {
  height: 100%;
  background: #f8f8fa;
}
</style>
