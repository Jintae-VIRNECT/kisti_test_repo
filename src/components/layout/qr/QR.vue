<template>
	<section>
		<transition name="app-fade" mode="out-in">
			<router-view />
		</transition>
		<TheFooter />
	</section>
</template>

<script>
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'
export default {
	data() {
		return {}
	},
	components: {
		TheFooter,
	},
}
</script>

<style lang="scss" scoped></style>
