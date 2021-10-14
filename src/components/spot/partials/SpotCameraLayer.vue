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
    cameraBlockList: {
      type: Object,
    },
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
  width: calc(100vh * 0.754);
  height: 100%;
  margin-right: 0.4rem;
  margin-left: 0.4rem;
  //aspect-ratio: 0.754/1;
  background: black;
  border: none;

  img {
    max-width: 100vh;
    height: 75%;
    transform: rotate(90deg);
  }
}
.fullscreen-camera-row {
  position: absolute;
  bottom: 2.777vh;
  display: flex;
  flex-direction: row;
  justify-content: center;
  width: 100vw;

  .spot-camera-block {
    //aspect-ratio: 1/0.75;
    width: calc(15.5vh / 0.75);
    height: 15.5vh;
    margin: 0 0.648vh;
    border: 2px solid #040404;
    border-radius: 3px;
  }
}
</style>
