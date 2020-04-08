<template>
  <figure class="role" v-if="role && role.length > 0">
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
}
</style>
