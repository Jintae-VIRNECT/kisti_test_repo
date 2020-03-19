<template>
  <nav class="header-workspace-lnb">
    <lnb-selector></lnb-selector>
  </nav>
</template>

<script>
import LnbSelector from '../tools/LnbSelector'
export default {
  name: 'HeaderWorkspaceLnb',
  components: {
    LnbSelector,
  },
  data() {
    return {}
  },

  /* Lifecycles */
  mounted() {},
}
</script>
