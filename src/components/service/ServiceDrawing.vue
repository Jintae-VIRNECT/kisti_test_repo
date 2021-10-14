<template>
  <div class="main-body drawing">
    <drawing-tools></drawing-tools>
    <menus v-if="!isMobileSize"></menus>
    <drawing></drawing>
  </div>
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
}
</script>
