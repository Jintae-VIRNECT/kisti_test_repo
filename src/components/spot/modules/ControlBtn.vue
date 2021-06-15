<template>
  <button class="spot-control-btn" :disabled="disabled" @click="clickListener">
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
  },
}
</script>

<style></style>
