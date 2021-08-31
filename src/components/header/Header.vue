<template>
  <transition name="header" mode="out-in">
    <workspace-header v-if="$route.name === 'workspace'"></workspace-header>
    <guest-header v-else-if="$route.name === 'connectioninfo'"></guest-header>
    <qr-header v-else-if="$route.name === 'qr'"></qr-header>
    <service-header v-else></service-header>
  </transition>
</template>

<script>
import WorkspaceHeader from './WorkspaceHeader'
import ServiceHeader from './ServiceHeader'
import GuestHeader from './GuestHeader'
import QrHeader from './QrHeader.vue'

export default {
  name: 'Header',
  components: {
    WorkspaceHeader,
    ServiceHeader,
    GuestHeader,
    QrHeader,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
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
