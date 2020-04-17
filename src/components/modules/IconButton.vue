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
  padding: 8px 15px 8px 40px;
  color: rgba(#d2d2d2, 0.8);
  font-size: 13px;
  line-height: 20px;
  background: #38383a 13px 50%/22px no-repeat;
  border-radius: 2px;
  opacity: 0.4;

  &:hover {
    opacity: 0.8;
  }
  &:active {
    opacity: 0.8;
  }
}
</style>
