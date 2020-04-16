<template>
  <div class="badge-container">
    <div class="badge-container__text">{{ listCount }}</div>
  </div>
</template>
<script>
export default {
  name: 'ListBadge',
  props: {
    listCount: {
      type: Number,
      default: 0,
    },
  },
}
</script>
<style lang="scss" scoped>
.badge-container {
  height: 22px;
  width: 32px;
  border-radius: 12px;
  background-color: black;
  display: flex;
  align-items: center;
  justify-content: center;
}
.badge-container__text {
  color: rgb(224, 224, 224);
  font-size: 14px;
  font-family: NotoSansCJKkr-Medium;
  font-weight: 500;
}
</style>
