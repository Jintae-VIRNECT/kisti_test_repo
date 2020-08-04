<template>
	<div id="app">
		<el-dialog
			class="login-service-modal"
			:visible.sync="show"
			width="420px"
			top="11vh"
			v-if="show"
		>
			<img src="~assets/images/common/img-moveto-login@2x.png" />
			<p class="popup-title">
				<strong>{{ $t('login.needTo.title') }}</strong>
			</p>
			<p>{{ $t('login.needTo.contents') }}</p>
		</el-dialog>
		<template v-else>
			<TheHeader :showSection="showSection" :logoUrl="logoUrl" />
			<transition name="app-fade" mode="out-in">
				<router-view />
			</transition>
		</template>
	</div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
export default {
	data() {
		return {
			show: false,
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
	methods: {
		loginService() {
			setTimeout(() => {
				this.show = false
			}, 2500)
		},
	},
	mounted() {
		const redirectTarget = this.$route.query.continue
		if (redirectTarget) {
			this.show = true
			this.loginService()
		}
	},
}
</script>

<style lang="scss" scoped></style>
