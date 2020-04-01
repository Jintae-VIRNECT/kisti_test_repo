<template>
	<div id="app">
		<!-- <header-section></header-section> -->
		<transition name="app-fade" mode="out-in">
			<router-view />
		</transition>
	</div>
</template>

<script>
// import headerSection from '@/components/layout/common/Header'
export default {
	components: {
		// headerSection,
	},
	computed: {
		currentUser() {
			return this.$store.state.user
		},
	},
	methods: {
		logOut() {
			this.$store.dispatch('auth/logout')
			this.$route.path = '/'
		},
	},
}
</script>

<style lang="scss" scoped>
</style>