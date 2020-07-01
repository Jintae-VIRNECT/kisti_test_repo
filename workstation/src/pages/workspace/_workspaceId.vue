<template>
  <div />
</template>

<script>
export default {
  fetch({ params, store }) {
    store.commit('workspace/SET_ACTIVE_WORKSPACE', params.workspaceId)
  },
  beforeMount() {
    this.$router.replace('/')
  },
}
</script>
