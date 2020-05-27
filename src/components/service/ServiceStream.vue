<template>
  <main class="main-wrapper stream">
    <div class="stream-body">
      <stream-tools></stream-tools>
      <menus></menus>
      <main-video></main-video>
    </div>
  </main>
</template>

<script>
import StreamTools from './tools/StreamTools'
import Menus from './tools/Menus'
import MainVideo from './stream/MainVideo'
export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
    Menus,
    MainVideo,
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
