<template>
	<section>
		<TheHeader :showSection="showSection" :auth="auth" :logoImg="logoImg">
			<template slot="subTitle">{{ $t('qrLogin.title') }}</template>
		</TheHeader>
		<transition name="app-fade" mode="out-in">
			<router-view :auth="auth" />
		</transition>
		<TheFooter v-if="$env !== 'onpremise'" />
	</section>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'
export default {
	components: {
		TheHeader,
		TheFooter,
	},
	props: {
		auth: Object,
	},
	data() {
		return {
			showSection: {
				login: true,
				profile: false,
			},
			qrImg: null,
		}
	},
	computed: {
		logoImg() {
			return this.$store.getters.logoImg
		},
	},
	async mounted() {
		try {
			if (this.auth.isLogin) {
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
