<template>
  <main class="main-wrapper stream">
    <div class="stream-body">
      <stream-tools></stream-tools>
      <main-video></main-video>
    </div>
  </main>
</template>

<script>
import StreamTools from './tools/StreamTools'
import MainVideo from './stream/MainVideo'
export default {
  name: 'ServiceStream',
  components: {
    StreamTools,
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
