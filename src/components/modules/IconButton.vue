<template>
  <div class="icon-button">
    <img class="icon-button__icon" :src="imgSrc" />
    <div class="icon-button__text">{{ text }}</div>
  </div>
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
  background: rgb(41, 41, 44);
  border-radius: 2px;
  width: 104px;
  height: 36px;

  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 40%;

  &:hover {
    background-color: rgb(56, 56, 58);
    opacity: 80%;
  }
  &:active {
    background-color: rgb(56, 56, 58);
    opacity: 80%;
  }

  .icon-button__icon {
    margin-right: 5px;
  }

  .icon-button__text {
    color: rgb(210, 210, 210);
    font-size: 13px;
    font-family: NotoSansCJKkr-Regular;
    font-weight: normal;
    width: 48px;
    height: 19px;
    text-align: center;
  }
}
</style>
