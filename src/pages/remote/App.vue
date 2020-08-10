<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png" /> -->
    <hello-world msg="remote" />
  </div>
</template>

<script>
import HelloWorld from '../../components/HelloWorld'

export default {
  name: 'App',
  components: {
    HelloWorld,
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
