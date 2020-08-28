<template>
  <main class="remote-layout offsetwidth">
    <remote-cooperation-list></remote-cooperation-list>
  </main>
</template>

<script>
import RemoteCooperationList from '../record/RemoteCooperationList'
export default {
  name: 'RemoteLayout',
  components: {
    RemoteCooperationList,
  },
}
</script>

<style lang="scss">
@import '~@/assets/style/remote.scss';
.remote-layout {
  min-height: calc(100vh - 50px);
}
.offsetwidth {
  position: relative;
  width: 100%;
  max-width: 66%;
  margin-right: auto;
  margin-left: auto;
}
</style>
