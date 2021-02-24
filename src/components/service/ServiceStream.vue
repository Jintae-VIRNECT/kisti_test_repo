<template>
  <div class="main-body stream">
    <stream-tools></stream-tools>
    <menus></menus>
    <main-video></main-video>
  </div>
</template>

<script>
import StreamTools from './tools/StreamTools'
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'

import { mapGetters } from 'vuex'

export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
    Menus,
    MainVideo,
  },
  computed: {
    ...mapGetters(['view']),
  },
}
</script>
