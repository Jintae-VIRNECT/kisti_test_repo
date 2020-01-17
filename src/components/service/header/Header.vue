<template>
<header class="header">
  <img class="header-logo" src="~assets/image/logo_symtext.svg"/>

  <header-nav></header-nav>

  <header-tools></header-tools>
</header>
</template>

<script>
import HeaderNav from './partials/HeaderSnb'
import HeaderTools from './partials/HeaderTools'
export default {
	name: "Header",
	components: {
    HeaderNav,
    HeaderTools
  },
	data() {
		return {}
	},
	computed: {},
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>