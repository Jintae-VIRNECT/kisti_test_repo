<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png" /> -->
    <remote-layout msg="remote" />
  </div>
</template>

<script>
// import RemoteLayout from '../components/layout/RemoteLayout'
import RemoteLayout from '../../components/layout/RemoteLayout'

export default {
  name: 'App',
  components: {
    RemoteLayout,
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
