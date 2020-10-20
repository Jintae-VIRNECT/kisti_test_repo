<template>
	<transition name="app-fade" mode="out-in">
		<router-view :showSection="showSection" :auth="auth" />
	</transition>
</template>

<script>
import Vue from 'vue'
import api from 'api/axios'
import auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'
import store from '@/store/index'
export default {
	async beforeRouteEnter(to, from, next) {
		if (to.query.lang) {
			const lang = to.query.lang
			await store.dispatch('CHANGE_LANG', lang)
		}
		let res = await api.getUrls()
		const environmentCss = 'font-size: 1.2rem;'
		console.log('%cprocess env: %s', environmentCss, res.env)
		Vue.prototype.$urls = res
		Vue.prototype.$env = res.env
		await auth.init({ env: res.env, urls: res })
		if (res.env === 'onpremise') {
			store.dispatch('SET_CUSTOM')
		}
		next()
	},
	data() {
		return {
			auth,
			showSection: {
				login: false,
				language: true,
			},
		}
	},
}
</script>

<style lang="scss" scoped></style>
