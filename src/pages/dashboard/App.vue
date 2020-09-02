<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png" /> -->
    <dash-board-layout msg="dashboard" />
  </div>
</template>

<script>
import DashBoardLayout from '../../components/layout/DashBoardLayout'

export default {
  name: 'App',
  components: {
    DashBoardLayout,
  },
}
</script>

<style></style>
