<template>
	<section>
		<TheHeader :showSection="showSection" :auth="auth">
			<template slot="subTitle">{{ $t('qrLogin.title') }}</template>
		</TheHeader>
		<transition name="app-fade" mode="out-in">
			<router-view :myInfo="myInfo" />
		</transition>
		<TheFooter />
	</section>
</template>

<script>
import Vue from 'vue'
import api from 'api/axios'
import auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'
export default {
	async beforeRouteEnter(to, from, next) {
		let res = await api.getUrls()
		Vue.prototype.$urls = res
		await auth.init({ env: res.env })
		next()
	},
	data() {
		return {
			auth,
			showSection: {
				login: true,
				profile: false,
			},
			myInfo: {},
			qrImg: null,
		}
	},
	components: {
		TheHeader,
		TheFooter,
	},
	async mounted() {
		try {
			if (auth.isLogin) {
				this.myInfo = auth.myInfo
				this.showSection.login = false
				this.showSection.link = true
				this.showSection.profile = true
			} else throw 'error'
		} catch (e) {
			this.showSection.login = true
			this.showSection.profile = false
			this.showSection.link = false
			location.replace(`${this.$urls['console']}/?continue=${location.href}`)
		}
	},
}
</script>

<style lang="scss" scoped></style>
