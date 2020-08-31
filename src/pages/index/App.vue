<template>
  <div id="app">
    <remote-layout />
    <remote-footer></remote-footer>
  </div>
</template>

<script>
import RemoteLayout from 'components/layout/RemoteLayout'
import RemoteFooter from 'components/footer/RemoteFooter'

export default {
  name: 'App',
  components: {
    RemoteLayout,
    RemoteFooter,
  },
}
</script>
