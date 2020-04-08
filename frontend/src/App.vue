<template>
	<div id="app">
		<TheHeader />
		<transition name="app-fade" mode="out-in">
			<router-view />
		</transition>
	</div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
export default {
	components: {
		TheHeader,
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

<style lang="scss" scoped></style>
