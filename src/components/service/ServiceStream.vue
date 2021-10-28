<template>
  <div class="main-body stream">
    <stream-tools
      v-if="!isMobileSize || (isMobileSize && viewForce)"
    ></stream-tools>
    <menus v-if="!isMobileSize"></menus>
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
    ...mapGetters(['viewForce']),
  },
}
</script>
