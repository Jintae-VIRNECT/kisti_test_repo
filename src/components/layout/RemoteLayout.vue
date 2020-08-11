<template>
  <div class="offsetwidth">
    <!-- <remote-header></remote-header> -->
    <remote-record-list></remote-record-list>
    <!-- <remote-footer></remote-footer> -->
  </div>
</template>

<script>
// import RemoteHeader from '../header/RemoteHeader'
// import RemoteFooter from '../footer/RemoteFooter'
import RemoteRecordList from '../record/RemoteRecordList'
export default {
  components: {
    // RemoteHeader,
    // RemoteFooter,
    RemoteRecordList,
  },
}
</script>

<style lang="scss">
@import '../../assets/style/layout.scss';
.offsetwidth {
  position: relative;
  width: 100%;
  max-width: 80%;
  margin-right: auto;
  margin-left: auto;
}
</style>
