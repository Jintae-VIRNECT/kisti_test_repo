<template>
  <section class="spot-camera-layer">
    <section class="mobile-front-camera-row">
      <spot-camera-block
        v-for="(cameraBlock, idx) in cameraBlockList['front']"
        :key="'f' + idx"
        :video="cameraBlock.video"
        :option="cameraBlock.option"
        :title="cameraBlock.title"
        :ratio="cameraBlock.ratio"
      ></spot-camera-block>
      <div class="spot-center-icon"></div>
    </section>
    <section class="mobile-side-back-camera-row">
      <spot-camera-block
        v-for="(cameraBlock, idx) in [
          ...cameraBlockList['side'],
          ...cameraBlockList['back'],
        ]"
        :key="'fsb' + idx"
        :video="cameraBlock.video"
        :option="cameraBlock.option"
        :title="cameraBlock.title"
        :ratio="cameraBlock.ratio"
      ></spot-camera-block>
    </section>
  </section>
</template>

<script>
import SpotCameraBlock from './SpotCameraBlock.vue'

export default {
  name: 'SpotCameraLayerMobile',
  components: { SpotCameraBlock },
  props: {
    cameraBlockList: {
      type: Object,
    },
  },
}
</script>

<style></style>
