<template>
  <main class="main-wrapper">
    <div class="mainview">
      <main-tools></main-tools>
      <main-video></main-video>
      <video-list></video-list>
    </div>
  </main>
</template>

<script>
import MainTools from './partials/MainTools'
import MainVideo from './MainVideo'
import VideoList from './VideoList'
export default {
  name: 'MainView',
  components: {
    MainTools,
    MainVideo,
    VideoList,
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
