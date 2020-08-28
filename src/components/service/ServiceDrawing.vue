<template>
  <div class="main-body drawing">
    <drawing-tools v-show="account.roleType === LEADER"></drawing-tools>
    <menus :viewType="viewType"></menus>
    <drawing></drawing>
  </div>
</template>

<script>
import DrawingTools from './tools/DrawingTools'
import Menus from './tools/Menus'
import Drawing from './drawing/Drawing'

import { ROLE } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'

export default {
  name: 'ServiceDrawing',
  components: {
    DrawingTools,
    Menus,
    Drawing,
  },
  data() {
    return {
      LEADER: ROLE.LEADER,
      viewType: VIEW.DRAWING,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
