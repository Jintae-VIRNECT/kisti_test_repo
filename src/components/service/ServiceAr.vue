<template>
  <main class="main-wrapper">
    <div class="ar-body">
      <ar-tools></ar-tools>
      <menus></menus>
      <ar-view></ar-view>
    </div>
  </main>
</template>

<script>
import ArTools from './tools/ArTools'
import Menus from './tools/Menus'
import ArView from './ar/ArView'
export default {
  name: 'ServiceAr',
  components: {
    ArTools,
    Menus,
    ArView,
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
