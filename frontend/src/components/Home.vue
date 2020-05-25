<template>
	<div id="app">
		<TheHeader :showSection="showSection" />
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
			},
		}
	},
	components: {
		TheHeader,
	},
}
</script>

<style lang="scss" scoped></style>
