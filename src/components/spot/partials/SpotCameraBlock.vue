<template>
  <figure
    class="spot-camera-block"
    :class="[
      ratio,
      {
        'no-video': noVideo,
        'upside-down': option ? option.upsideDown : false,
      },
    ]"
  >
    <figcaption class="camera-title">
      {{ title }}
    </figcaption>
    <img :id="`video-canvas_${title}`" />
  </figure>
</template>

<script>
export default {
  props: {
    video: {
      type: String,
    },
    title: {
      type: String,
    },
    ratio: {
      default: 'landscape',
      type: String,
    },
    option: {
      type: Object,
    },
  },
  watch: {
    video(val) {
      if (val === null) this.noVideo = true
      else {
        document.getElementById(`video-canvas_${this.title}`).src = this.video
      }
    },
  },
  data() {
    return {
      noVideo: true,
      resource: null,

      clearRect: [],
      translate: [],
      drawImage: [0, 0],
    }
  },
  methods: {
    setOption(clientWidth, clientHeight) {
      this.clearRect = new Array(0, 0, clientWidth, clientHeight)
      this.translate = new Array(clientWidth / 2, clientHeight / 2)
      const max = Math.max(clientWidth, clientHeight)
      const min = max === clientWidth ? clientHeight : clientWidth
      this.drawImage = new Array(-(max / 2), -(min / 2))
    },
  },
}
</script>

<style></style>
