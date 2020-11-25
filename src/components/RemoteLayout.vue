<template>
  <!-- <div class="service-wrapper">Hello World</div> -->
  <router-view :key="key"></router-view>
</template>

<script>
export default {
  name: 'RemoteLayout',
  data() {
    return {
      key: 1,
    }
  },
  created() {
    this.$eventBus.$on('reJoin', () => {
      this.key++
    })
  },
  beforeDestroy() {
    this.$eventBus.$off('reJoin', () => {
      this.key++
    })
  },
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
