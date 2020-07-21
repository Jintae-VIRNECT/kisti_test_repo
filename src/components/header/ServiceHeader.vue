<template>
  <header class="header">
    <div class="header-service">
      <img class="header-logo" src="~assets/image/logo_symtext.svg" />
      <header-lnb></header-lnb>

      <header-tools></header-tools>
    </div>
  </header>
</template>

<script>
import HeaderLnb from './partials/HeaderServiceLnb'
import HeaderTools from './partials/HeaderServiceTools'
export default {
  name: 'ServiceHeader',
  components: {
    HeaderLnb,
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
