<template>
  <figure
    class="spot-camera-block"
    :class="[
      ratio,
      {
        'no-video': noVideo,
      },
    ]"
  >
    <figcaption class="camera-title">
      {{ title }}
    </figcaption>
    <canvas ref="video-canvas"></canvas>
  </figure>
</template>

<script>
let resource = null

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
      default: () => {
        return {
          clearRect: [],
          translate: [],
          rotate: null,
          drawImage: [0, 0],
        }
      },
    },
  },
  watch: {
    video(val) {
      if (val === null) this.noVideo = true
      else {
        resource.src = this.video
      }
    },
  },
  data() {
    return {
      noVideo: true,
    }
  },
  methods: {},
  async created() {
    await this.$nextTick()
    const canvas = this.$refs['video-canvas']
    resource = new Image()
    resource.onload = () => {
      const { clearRect, translate, rotate, drawImage } = this.option

      if (clearRect.length) {
        canvas.clearRect(...clearRect)
        canvas.save()
      }

      if (translate.length) canvas.translate(...translate)
      if (rotate) canvas.rotate(rotate)

      canvas.drawImage(resource, ...drawImage)

      if (clearRect.length && translate.length && rotate) canvas.restore()
    }
  },
}
</script>

<style></style>
