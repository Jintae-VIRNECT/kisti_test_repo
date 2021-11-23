<template>
  <button
    class="spot-control-btn"
    :disabled="disabled"
    @click="clickListener"
    @mousedown="mousedown"
    @mouseup="mouseup"
    @touchstart="touchstart"
    @touchend="touchend"
  >
    <div class="back">
      <img class="icon" :src="imgSrc" />
    </div>
  </button>
</template>

<script>
export default {
  name: 'ControlBtn',
  props: {
    imgSrc: {
      type: String,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    clickListener(evt) {
      evt.preventDefault()
      this.$emit('click')
    },
    mouseup(evt) {
      evt.preventDefault()
      this.$emit('mouseup')
    },
    mousedown(evt) {
      evt.preventDefault()
      this.$emit('mousedown')
    },
    touchstart(evt) {
      evt.preventDefault()
      this.$emit('touchstart')
    },
    touchend(evt) {
      evt.preventDefault()
      this.$emit('touchend')
    },
  },
}
</script>

<style></style>
