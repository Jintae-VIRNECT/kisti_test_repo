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
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 4px;
  box-shadow: 0px 2px 4px 0px rgba(11, 31, 72, 0.22);
}
</style>
