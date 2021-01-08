<template>
  <div class="main-body drawing">
    <drawing-tools v-show="account.roleType === LEADER"></drawing-tools>
    <menus></menus>
    <drawing></drawing>
  </div>
</template>

<script>
import DrawingTools from './tools/DrawingTools'
import Menus from './tools/Menus'
import Drawing from './drawing/Drawing'

import { ROLE } from 'configs/remote.config'

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
    }
  },
}
</script>
