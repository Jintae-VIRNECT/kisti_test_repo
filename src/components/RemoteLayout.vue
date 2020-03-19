<template>
  <section class="remote-layout">
    <!-- <div class="service-wrapper">Hello World</div> -->
    <header-section></header-section>
    <router-view></router-view>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'

export default {
  name: 'RemoteLayout',
  components: {
    HeaderSection,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {
    this.$moment.locale('ko')
  },
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
