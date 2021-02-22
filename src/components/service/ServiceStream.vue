<template>
  <div class="main-body stream">
    <stream-tools v-if="isStreamView"></stream-tools>
    <screen-stop-button v-else></screen-stop-button>

    <menus></menus>
    <main-video></main-video>
  </div>
</template>

<script>
import StreamTools from './tools/StreamTools'
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'
import ScreenStopButton from './stream/ScreenStopButton'
import { VIEW } from 'configs/view.config'

import { mapGetters } from 'vuex'

export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
    Menus,
    MainVideo,
    ScreenStopButton,
  },
  computed: {
    ...mapGetters(['view']),
    isStreamView() {
      return this.view === VIEW.STREAM
    },
  },
}
</script>
