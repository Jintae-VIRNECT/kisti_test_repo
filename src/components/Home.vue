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
			<TheHeader :showSection="showSection" :logoUrl="logoUrl">
				<template slot="subTitle">{{ $t('login.subTitle') }}</template>
			</TheHeader>
			<transition name="app-fade" mode="out-in">
				<router-view />
			</transition>
		</template>
	</div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import { Url } from 'WC-Modules/javascript/api/virnectPlatform/urls'
export default {
	data() {
		return {
			show: false,
			showSection: {
				login: false,
				language: true,
			},
			logoUrl: 'https://www.virnect.com',
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
		const urls = new Url(process.env.TARGET_ENV)

		// 자신으로 리다이렉트 제외
		if (
			redirectTarget === location.origin ||
			redirectTarget === location.origin + '/'
		) {
			location.href = '/'
			return false
		}

		// 로그인 필요 다이얼로그
		const needNotLogin = redirectTarget && redirectTarget.match(urls.www)
		if (redirectTarget && !needNotLogin) {
			this.show = true
			this.loginService()
		}
	},
}
</script>

<style lang="scss" scoped></style>
