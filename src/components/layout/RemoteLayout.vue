<template>
  <div class="remote-layout offsetwidth">
    <!-- <remote-header></remote-header> -->
    <remote-record-list></remote-record-list>
  </div>
</template>

<script>
// import RemoteHeader from '../header/RemoteHeader'

import RemoteRecordList from '../record/RemoteRecordList'
export default {
  name: 'RemoteLayout',
  components: {
    // RemoteHeader,

    RemoteRecordList,
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
