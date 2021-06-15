<template>
  <button
    class="spot-control-btn"
    :disabled="disabled"
    @click="clickListener"
    @mousedown="mousedown"
    @mouseup="mouseup">
    <div class="back">
      <img class="icon" :src="imgSrc" />
    </div>
  </button>
</template>

<script>
export default {
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
    clickListener() {
      this.$emit('click')
    },
    mouseup() {
      this.$emit('mouseup')
    },
    mousedown() {
      this.$emit('mousedown')
    },
  },
}
</script>

<style></style>
