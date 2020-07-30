<template>
  <div class="main-body ar">
    <ar-tools></ar-tools>
    <menus :viewType="viewType"></menus>
    <ar-view></ar-view>
  </div>
</template>

<script>
import ArTools from './tools/ArTools'
import Menus from './tools/Menus'
import ArView from './ar/ArView'
import { VIEW } from 'configs/view.config'
export default {
  name: 'ServiceAr',
  components: {
    ArTools,
    Menus,
    ArView,
  },
  data() {
    return {
      viewType: VIEW.AR,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
