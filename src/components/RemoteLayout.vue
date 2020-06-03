<template>
  <!-- <div class="service-wrapper">Hello World</div> -->
  <router-view></router-view>
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
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
