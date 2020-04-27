<template>
  <button
    class="icon-button"
    @click="$listeners['click']"
    :style="{ 'background-image': `url(${imgSrc})` }"
  >
    {{ text }}
  </button>
</template>

<script>
export default {
  name: 'IconButton',
  props: {
    text: {
      type: String,
      default: '',
    },
    imgSrc: {
      tpye: String,
      default: '',
    },
  },
}
</script>

<style lang="scss" scoped>
.icon-button {
  margin-left: 9px;
  padding: 0.571em 1.071em 0.571em 2.857em;
  color: rgba(#d2d2d2, 0.8);
  font-size: 0.929em;
  line-height: 1.429em;
  background: #38383a 0.929em 50%/1.571em no-repeat;
  border-radius: 2px;
  opacity: 0.4;

  &:hover {
    opacity: 0.8;
  }
  &:active {
    opacity: 1;
  }
}
</style>
