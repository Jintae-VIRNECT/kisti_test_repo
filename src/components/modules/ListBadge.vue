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
@import '~assets/style/vars';

.badge-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.286rem;
  height: 1.571rem;
  background-color: $color_darkgray_1000;
  border-radius: 0.857rem;
}
.badge-container__text {
  color: $color_text_sub;
  font-weight: 500;
  font-size: 1rem;
}
</style>
