<template>
	<section>
		<TheHeader :showSection="showSection" :runtimeInfo="runtimeInfo">
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
import Auth from 'api/virnectPlatformAuth'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'
export default {
	async beforeRouteEnter(to, from, next) {
		let res = await api.getUrls()
		Vue.prototype.$urls = res
		next(vm => {
			vm.runtimeInfo = {
				urls: res,
				env: res.env,
			}
		})
	},
	data() {
		return {
			showSection: {
				login: true,
				profile: false,
			},
			runtimeInfo: {
				urls: null,
				env: null,
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
			await Auth.init()
			if (Auth.isLogin) {
				this.myInfo = Auth.myInfo
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
