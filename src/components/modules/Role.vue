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
  margin: auto 0 auto 0.857em;
  padding: 0.357em 0.714em;
  color: #bfddff;
  font-weight: 500;
  font-size: 0.857em;
  font-family: 'roboto';
  line-height: 0.857em;
  border: solid 1px #bfddff;
  border-radius: 0.857em;

  &.card {
    color: #f2f2f2;
    background: rgb(56, 56, 58);
    border: 1px solid rgb(204, 204, 204);
  }

  &.expired {
    color: #f2f2f2;
    background: rgba(204, 23, 1, 0.3);
    border: 1px solid rgb(204, 23, 1);
    border-radius: 0.929em;
  }
}
</style>
