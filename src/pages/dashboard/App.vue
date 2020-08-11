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

<style>
#app {
  margin-top: 60px;
  color: #2c3e50;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
