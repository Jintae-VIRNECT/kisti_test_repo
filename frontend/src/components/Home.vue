<template>
	<div id="app">
		<TheHeader :showSection="showSection" :logoUrl="logoUrl" />
		<transition name="app-fade" mode="out-in">
			<router-view />
		</transition>
	</div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
export default {
	data() {
		return {
			showSection: {
				login: false,
				language: true,
			},
			logoUrl: '/',
		}
	},
	components: {
		TheHeader,
	},
}
</script>

<style lang="scss" scoped></style>
