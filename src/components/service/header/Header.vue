<template>
<header class="header-wrapper">
  <img class="header-logo" src="~assets/image/logo_symtext.svg"/>

  <header-snb></header-snb>

  <header-tools></header-tools>
</header>
</template>

<script>
import HeaderSnb from './partials/HeaderSnb'
import HeaderTools from './partials/HeaderTools'
export default {
	name: "Header",
	components: {
    HeaderSnb,
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