<template>
  <button class="drawing-lock-btn" @click="clickHandler">
    <img src="~assets/image/call/drawing_lock_new.svg" alt="lock" />
  </button>
</template>

<script>
export default {
  methods: {
    clickHandler() {
      this.$emit('click')
    },
  },
}
</script>

<style scoped>
.drawing-lock-btn {
  width: 4rem;
  height: 4rem;
}
</style>
