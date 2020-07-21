<template>
  <header class="header">
    <div class="header-workspace">
      <img class="header-logo" src="~assets/image/logo_symtext.svg" />
      <!-- <div class="header-divider"></div>
    <header-nav></header-nav> -->

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
// import HeaderNav from './partials/HeaderWorkspaceNav'
import HeaderTools from './partials/HeaderWorkspaceTools'
export default {
  name: 'WorkspaceHeader',
  components: {
    // HeaderNav,
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
