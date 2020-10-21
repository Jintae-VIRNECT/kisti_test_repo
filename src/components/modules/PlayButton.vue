<template>
  <button
    class="play-button"
    @click="clickListener"
    @mouseover="select = true"
    @mouseleave="select = false"
  >
    <img :src="imgSrc" />
  </button>
</template>

<script>
export default {
  name: 'PlayButton',
  data() {
    return {
      select: false,
    }
  },
  props: {
    mediaUrl: {
      type: String,
      default: null,
    },
  },
  computed: {
    imgSrc() {
      return this.select
        ? require('assets/image/ic_icon_play_on.svg')
        : require('assets/image/ic_icon_play_off.svg')
    },
  },
  methods: {
    clickListener() {
      if (typeof this.$listeners['click'] === 'function') {
        this.$listeners['click']()
        this.animateClass = this.animation
        setTimeout(() => {
          this.animateClass = ''
        }, 400)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.play-button {
  width: 30px;
  height: 30px;
}
</style>
