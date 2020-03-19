<template>
  <div class="header-workspace">
    <div class="header-divider"></div>
    <header-nav></header-nav>

    <header-tools></header-tools>
  </div>
</template>

<script>
import HeaderNav from './partials/HeaderWorkspaceNav'
import HeaderTools from './partials/HeaderWorkspaceTools'
export default {
  name: 'WorkspaceHeader',
  components: {
    HeaderNav,
    HeaderTools,
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
