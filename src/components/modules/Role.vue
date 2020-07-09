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
  height: 1.43rem;
  margin: auto 0 auto 0.857rem;
  padding: 0.357em 0.714em;
  color: $color_primary_400;
  font-weight: 500;
  font-size: 0.857rem;
  font-family: 'Roboto';
  line-height: 0.857rem;
  border: solid 1px $color_primary_400;
  border-radius: 0.857rem;

  &.card {
    color: #f2f2f2;
    background: $color_darkgray_400;
    border: 1px solid #cccccc;
  }

  &.expired {
    color: #f2f2f2;
    font-family: Noto Sans KR;
    background: rgba(#cc1701, 0.3);
    border: 1px solid #cc1701;
    border-radius: 0.929rem;
  }
}
</style>
