<template>
  <div class="remote-layout offsetwidth">
    <!-- <remote-header></remote-header> -->
    <remote-cooperation-list></remote-cooperation-list>
  </div>
</template>

<script>
// import RemoteHeader from '../header/RemoteHeader'

import RemoteCooperationList from '../record/RemoteCooperationList'
export default {
  name: 'RemoteLayout',
  components: {
    // RemoteHeader,

    RemoteCooperationList,
  },
}
</script>

<style lang="scss">
@import '~@/assets/style/remote.scss';
.remote-layout {
  // height: 1080px;
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
