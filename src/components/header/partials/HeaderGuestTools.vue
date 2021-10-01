<template>
  <ul class="header-workspace-tools">
    <stream></stream>
    <mic></mic>
    <speaker></speaker>

    <profile
      :class="{
        'workspace-select-tools': !hasWorkspace,
      }"
      :isGuest="true"
    >
      ></profile
    >
  </ul>
</template>

<script>
import Stream from '../tools/Stream'
import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'
import Profile from '../tools/HeaderProfile'

export default {
  name: 'HeaderTools',
  components: {
    Stream,
    Mic,
    Speaker,

    Profile,
  },
  computed: {
    hasWorkspace() {
      return this.workspace && this.workspace.uuid && this.hasLicense
    },
  },
}
</script>
