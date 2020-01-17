<template>
<section>
	<!-- <div class="service-wrapper">Hello World</div> -->
  <header-section></header-section>
  <router-view></router-view>
</section>
</template>

<script>
import HeaderSection from './header/Header'

export default {
	name: "ServiceLayout",
	components: {
    HeaderSection
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
  }
}
</script>

<style lang="scss">
@import "~assets/style/service";
</style>
