<template>
  <div id="app">
    <!-- <img alt="Vue logo" src="./assets/logo.png" /> -->
    <remote-layout />
    <remote-footer></remote-footer>
  </div>
</template>

<script>
// import RemoteLayout from '../components/layout/RemoteLayout'
import RemoteLayout from '../../components/layout/RemoteLayout'
import RemoteFooter from '../../components/footer/RemoteFooter'

export default {
  name: 'App',
  components: {
    RemoteLayout,
    RemoteFooter,
  },
}
</script>

<style lang="scss">
@import '../../assets/style/layout.scss';
</style>
