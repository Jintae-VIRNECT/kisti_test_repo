<template>
  <figure class="role" v-if="role && role.length > 0" :class="opt">
    {{ role }}
  </figure>
</template>

<script>
export default {
  name: 'Role',
  props: {
    role: {
      type: String,
      default: '',
    },
    opt: {
      type: String,
      validator: value => ['', 'card', 'expired'].indexOf(value) >= 0,
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
.role {
  height: fit-content;
  margin: auto 0 auto 12px;
  padding: 5px 10px;
  color: #bfddff;
  font-weight: 500;
  font-size: 12px;
  font-family: 'roboto';
  line-height: 12px;
  border: solid 1px #bfddff;
  border-radius: 12px;

  &.card {
    font-family: NotoSansCJKkr-Medium;
    color: rgb(242, 242, 242);
    background: rgb(56, 56, 58);
    border: 1px solid rgb(204, 204, 204);
  }

  &.expired {
    font-family: NotoSansCJKkr-Medium;
    color: rgb(242, 242, 242);
    background: rgba(204, 23, 1, 0.3);
    border: 1px solid rgb(204, 23, 1);
    border-radius: 13px;
  }
}
</style>
