<template>
  <button class="fab" @click="toggleScroll">
    <img src="~assets/image/ic_arrow_up.svg" alt="arrow" />
  </button>
</template>

<script>
export default {
  name: 'Fab',
  methods: {
    toggleScroll() {
      this.$eventBus.$emit('scroll:reset:dashboard')
    },
  },
}
</script>

<style lang="scss">
.fab {
  position: fixed;
  right: 40px;
  bottom: 80px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 50%;
  box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);
  transition: 0.2s;
  &:active {
    background: rgba(224, 224, 224, 0.774);
  }
}
</style>
