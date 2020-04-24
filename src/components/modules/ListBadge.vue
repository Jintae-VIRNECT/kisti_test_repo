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
      type: [Number, String],
      default: 0,
    },
  },
}
</script>
<style lang="scss" scoped>
.badge-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 22px;
  background-color: black;
  border-radius: 12px;
}
.badge-container__text {
  color: rgb(224, 224, 224);
  font-weight: 500;
  font-size: 14px;
}
</style>
