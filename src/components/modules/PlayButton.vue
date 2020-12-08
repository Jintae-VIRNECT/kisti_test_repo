<template>
  <button
    class="play-button"
    @click.stop="clickListener"
    @mouseover="select = true"
    @mouseleave="select = false"
  >
    <img
      :src="imgSrc"
      :style="{
        width: size,
        height: size,
      }"
    />
  </button>
</template>

<script>
export default {
  name: 'PlayButton',
  props: {
    mediaUrl: {
      type: String,
      default: null,
    },
    size: {
      type: [Number, String],
      default: 12,
    },
  },
  data() {
    return {
      select: false,
    }
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
@import '~assets/style/mixin';
.play-button {
  // @include ir();
  // width: 23.996px;
  // height: 23.996px;
  background: center/50% no-repeat;
}
</style>
