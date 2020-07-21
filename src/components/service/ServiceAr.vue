<template>
  <div class="main-body ar">
    <ar-tools></ar-tools>
    <menus></menus>
    <ar-view></ar-view>
  </div>
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
