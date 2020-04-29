<template>
  <main class="main-wrapper mainview">
    <div class="mainview-body">
      <main-tools></main-tools>
      <main-video></main-video>
    </div>
  </main>
</template>

<script>
import MainTools from './partials/MainTools'
import MainVideo from './MainVideo'
export default {
  name: 'MainView',
  components: {
    MainTools,
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
