<template>
  <transition name="header" mode="out-in">
    <workspace-header v-if="$route.name === 'workspace'"></workspace-header>

    <service-header v-else></service-header>
  </transition>
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
    // console.log(this.$route)
  },
}
</script>
<style lang="scss" src="assets/style/header.scss"></style>

<style>
.header-enter-active {
  transition: 0.4s transform ease 0.2s;
}
.header-enter {
  transform: translateY(-80px);
}
.header-enter-to {
  transform: translateY(0);
}
</style>
