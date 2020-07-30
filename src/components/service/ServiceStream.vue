<template>
  <div class="main-body stream">
    <stream-tools></stream-tools>
    <menus :viewType="viewType"></menus>
    <main-video></main-video>
  </div>
</template>

<script>
import StreamTools from './tools/StreamTools'
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'
import { VIEW } from 'configs/view.config'
export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
    Menus,
    MainVideo,
  },
  data() {
    return {
      viewType: VIEW.STREAM,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
