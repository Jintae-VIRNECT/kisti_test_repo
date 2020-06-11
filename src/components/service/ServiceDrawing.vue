<template>
  <main class="main-wrapper drawing">
    <div class="drawing-body">
      <drawing-tools></drawing-tools>
      <menus></menus>
      <drawing></drawing>
    </div>
  </main>
</template>

<script>
import DrawingTools from './tools/DrawingTools'
import Menus from './tools/Menus'
import Drawing from './drawing/Drawing'

export default {
  name: 'ServiceDrawing',
  components: {
    DrawingTools,
    Menus,
    Drawing,
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
