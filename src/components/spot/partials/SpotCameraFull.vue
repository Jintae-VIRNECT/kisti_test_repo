<template>
  <section class="spot-camera-full">
    <spot-camera-block
      ratio="potrait"
      :title="cameraBlockList['front'][0].title"
    ></spot-camera-block>
    <spot-camera-block
      ratio="potrait"
      :title="cameraBlockList['front'][1].title"
    ></spot-camera-block>
  </section>
</template>

<script>
import SpotCameraBlock from './SpotCameraBlock'

export default {
  components: {
    SpotCameraBlock,
  },
  data() {
    return {
      cameraBlockList: Object.freeze({
        front: [
          {
            title: 'Front Camera 01',
            ratio: 'potrait',
            video: null,
          },
          {
            title: 'Front Camera 02',
            ratio: 'potrait',
            video: null,
          },
        ],
        side: [
          {
            title: 'Left Camera',
            ratio: 'landscape',
            video: null,
          },
          {
            title: 'Right Camera',
            ratio: 'landscape',
            video: null,
          },
        ],
        back: [
          {
            title: 'Back Camera',
            ratio: 'landscape',
            video: null,
          },
        ],
      }),
    }
  },
}
</script>

<style lang="scss">
.spot-camera-full {
  height: 100vh;

  display: flex;
  flex-direction: row;
  justify-content: center;

  height: 100vh;
}
</style>
