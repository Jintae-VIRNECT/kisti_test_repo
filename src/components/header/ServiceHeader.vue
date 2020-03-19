<template>
  <div class="header-service">
    <header-lnb></header-lnb>

    <header-tools></header-tools>
  </div>
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
