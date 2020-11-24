<template>
  <ul class="header-workspace-tools">
    <template v-if="hasWorkspace">
      <stream></stream>
      <mic></mic>
      <speaker></speaker>
    </template>
    <notice></notice>
    <profile></profile>
  </ul>
</template>

<script>
import { mapGetters } from 'vuex'

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
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['mainView', 'mute']),
    hasWorkspace() {
      return this.workspace && this.workspace.uuid && this.hasLicense
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
