<template>
  <section class="spot-camera-layer">
    <section
      class="front-camera-row"
      :class="{ 'full-camera-row-fullmode': isSpotFullscreen }"
    >
      <spot-camera-block
        :class="{ 'front-camera-fullmode': isSpotFullscreen }"
        v-for="(cameraBlock, idx) in cameraBlockList['front']"
        :key="'f' + idx"
        :video="cameraBlock.video"
        :option="cameraBlock.option"
        :title="cameraBlock.title"
        :ratio="cameraBlock.ratio"
      ></spot-camera-block>
    </section>

    <section class="side-camera-row" v-if="!isSpotFullscreen">
      <spot-camera-block
        v-for="(cameraBlock, idx) in cameraBlockList['side']"
        :key="'s' + idx"
        :video="cameraBlock.video"
        :option="cameraBlock.option"
        :title="cameraBlock.title"
        :ratio="cameraBlock.ratio"
      ></spot-camera-block>
    </section>

    <section class="back-camera-row" v-if="!isSpotFullscreen">
      <spot-camera-block
        v-for="(cameraBlock, idx) in cameraBlockList['back']"
        :key="'s' + idx"
        :video="cameraBlock.video"
        :option="cameraBlock.option"
        :title="cameraBlock.title"
        :ratio="cameraBlock.ratio"
      ></spot-camera-block>
    </section>

    <section class="fullscreen-camera-row" v-if="isSpotFullscreen">
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
import { mapGetters } from 'vuex'
import SpotCameraBlock from './SpotCameraBlock.vue'
export default {
  components: { SpotCameraBlock },
  props: {
    frontLeftImage: {
      type: String,
    },
    frontRightImage: {
      type: String,
    },
    sideLeftImage: {
      type: String,
    },
    sideRightImage: {
      type: String,
    },
    backImage: {
      type: String,
    },
  },
  data() {
    return {
      cameraBlockList: {
        front: [
          {
            name: 'fl',
            title: 'Front Camera 01',
            ratio: 'potrait',
            video: this.frontLeft,
            option: {
              //clearRect: [0, 0, 480, 640],
              //translate: [240, 320],
              //rotate: (90 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
          {
            name: 'fr',
            title: 'Front Camera 02',
            ratio: 'potrait',
            video: null,
            option: {
              //clearRect: [0, 0, 480, 640],
              //translate: [240, 320],
              //rotate: (90 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
        ],
        side: [
          {
            name: 'sl',
            title: 'Left Camera',
            ratio: 'landscape',
            video: null,
            option: null,
          },
          {
            name: 'sr',
            title: 'Right Camera',
            ratio: 'landscape',
            video: null,
            option: {
              upsideDown: true,
              //clearRect: [0, 0, 640, 480],
              //translate: [320, 240],
              //rotate: (180 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
        ],
        back: [
          {
            name: 'b',
            title: 'Back Camera',
            ratio: 'landscape',
            video: null,
            option: null,
          },
        ],
      },
    }
  },
  watch: {
    frontLeftImage(val) {
      this.cameraBlockList['front'][0].video = val
    },
    frontRightImage(val) {
      this.cameraBlockList['front'][1].video = val
    },
    sideLeftImage(val) {
      this.cameraBlockList['side'][0].video = val
    },
    sideRightImage(val) {
      this.cameraBlockList['side'][1].video = val
    },
    backImage(val) {
      this.cameraBlockList['back'][0].video = val
    },
  },
  computed: {
    ...mapGetters(['isSpotFullscreen']),
  },
}
</script>

<style lang="scss">
.full-camera-row-fullmode {
  background: black;
}
.spot-camera-block.front-camera-fullmode {
  border: none;
  margin-left: 4px;
  margin-right: 4px;

  height: 100vh;
  aspect-ratio: 0.754/1;
}
.fullscreen-camera-row {
  position: absolute;
  bottom: 2.777vh;
  width: 100vw;

  display: flex;
  flex-direction: row;
  justify-content: center;

  .spot-camera-block {
    margin: 0 0.648vh;

    border: 2px solid #040404;
    border-radius: 3px;

    height: 15.5vh;
    aspect-ratio: 1/0.75;
  }
}
</style>
