<template>
  <main
    class="main-wrapper"
    :class="{ shareview: account.roleType === EXPERT_LEADER }"
  >
    <div class="drawing-body">
      <drawing-tools
        v-show="account.roleType === EXPERT_LEADER"
      ></drawing-tools>
      <menus></menus>
      <drawing></drawing>
    </div>
  </main>
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
      EXPERT_LEADER: ROLE.EXPERT_LEADER,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
