<template>
  <main class="remote-layout offsetwidth">
    <remote-cooperation-list></remote-cooperation-list>
  </main>
</template>

<script>
import RemoteCooperationList from 'components/record/RemoteCooperationList'
export default {
  name: 'RemoteLayout',
  components: {
    RemoteCooperationList,
  },
}
</script>

<style lang="scss">
@import '~assets/style/layout.scss';
@import '~assets/style/remote.scss';
// @import '~assets/style/_vars.scss';
.remote-layout {
  min-height: calc(100vh - 3.5714rem);
}
.offsetwidth {
  position: relative;
  width: 100%;
  max-width: 66%;
  margin-right: auto;
  margin-left: auto;
}
</style>
