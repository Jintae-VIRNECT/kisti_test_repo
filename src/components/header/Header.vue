<template>
  <header class="header">
    <img class="header-logo" src="~assets/image/logo_symtext.svg" />

    <workspace-header v-if="$route.name === 'workspace'"></workspace-header>

    <service-header v-else></service-header>
  </header>
</template>

<script>
import WorkspaceHeader from './WorkspaceHeader'
import ServiceHeader from './ServiceHeader'
export default {
  name: 'Header',
  components: {
    WorkspaceHeader,
    ServiceHeader,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {
    console.log(this.$route)
  },
}
</script>
<style lang="scss" src="assets/style/header.scss"></style>
