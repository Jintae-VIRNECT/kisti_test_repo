<template>
  <div class="main-video__empty-inner loading">
    <img :src="videoLoadingSrc" />
    <p v-html="$t('service.stream_connecting')"></p>
  </div>
</template>

<script>
export default {
  name: 'VideoLoading',
  computed: {
    videoLoadingSrc() {
      return this.isMobileSize
        ? require('assets/image/call/mdpi_image_videolink_new.svg')
        : require('assets/image/call/mdpi_image_videolink_pc.svg')
    },
  },
}
</script>

<style></style>
