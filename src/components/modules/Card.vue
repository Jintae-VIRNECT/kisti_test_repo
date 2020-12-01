<template>
  <div class="card" :class="[customClass]">
    <slot name="header"></slot>
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'Card',
  props: {
    customClass: {
      type: String,
      default: null,
    },
  },
}
</script>

<style lang="scss">
.card {
  min-width: 21.4286rem;
  min-height: 22.1429rem;
  background: #ffffff;
  border: 1px solid #e3e3e3;
  border-radius: 10px;
  box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.04);
}
</style>
