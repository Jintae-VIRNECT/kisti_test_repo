<template>
  <div class="contents">
    <!-- <scroller height="800px"> -->
    <router-view></router-view>
    <!-- </scroller> -->
  </div>
</template>

<script>
// import Scroller from 'components/modules/Scroller'
export default {
  components: {
    // Scroller,
  },
}
</script>

<style lang="scss">
.contents {
  // width: 100%;

  height: 100%;
  padding-top: 78px;
  padding-right: 30px;
  padding-left: 280px;
}
</style>
