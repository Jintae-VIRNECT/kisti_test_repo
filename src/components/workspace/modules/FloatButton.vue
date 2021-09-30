<template>
  <button class="float-button" @click="onClick">
    <img :src="img" alt="icon" />
  </button>
</template>

<script>
export default {
  props: {
    img: {
      type: String,
      default: '',
    },
  },
  methods: {
    onClick() {
      this.$emit('onClick')
    },
  },
}
</script>

<style lang="scss" scoped>
.float-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 14px;
  > img {
    width: 24px;
    height: 24px;
  }
}
</style>
