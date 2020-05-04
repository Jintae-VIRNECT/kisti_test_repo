<template>
	<div id="app">
		<transition name="app-fade" mode="out-in">
			<router-view />
		</transition>
	</div>
</template>

<script>
export default {
	data() {
		return {}
	},
}
</script>
