<template>
	<section>
		<TheHeader :showSection="showSection">
			<template slot="subTitle">QR 로그인 센터</template>
		</TheHeader>
		<transition name="app-fade" mode="out-in">
			<router-view :myInfo="myInfo" />
		</transition>
		<TheFooter />
	</section>
</template>

<script>
import Auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'
// import Auth from 'api/virnectPlatformAuth'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'
export default {
	data() {
		return {
			showSection: {
				login: true,
				profile: false,
			},
			auth: Auth,
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
			if (Auth.myInfo) {
				this.myInfo = Auth.myInfo
				this.showSection.login = false
				this.showSection.profile = true
			} else throw 'error'
		} catch (e) {
			this.showSection.login = true
			this.showSection.profile = false
		}
	},
}
</script>

<style lang="scss" scoped></style>
