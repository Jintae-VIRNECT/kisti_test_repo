<template>
  <ul class="header-workspace-tools">
    <template v-if="hasWorkspace">
      <stream></stream>
      <mic></mic>
      <speaker></speaker>
    </template>
    <notice
      :class="{
        'tools-mobile': !hasWorkspace,
      }"
    ></notice>
    <profile
      :class="{
        'tools-mobile': !hasWorkspace,
      }"
    >
      ></profile
    >
  </ul>
</template>

<script>
import Stream from '../tools/Stream'
import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Notice from '../tools/Notice'
import Profile from '../tools/HeaderProfile'

export default {
  name: 'HeaderTools',
  components: {
    Stream,
    Mic,
    Speaker,
    Notice,
    Profile,
  },
  computed: {
    hasWorkspace() {
      return this.workspace && this.workspace.uuid && this.hasLicense
    },
  },
}
</script>
