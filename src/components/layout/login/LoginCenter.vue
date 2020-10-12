<template>
	<div class="home-section">
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
			<TheHeader :showSection="showSection" :auth="auth">
				<template slot="subTitle">{{ $t('login.subTitle') }}</template>
			</TheHeader>
			<transition name="app-fade" mode="out-in">
				<router-view :auth="auth" />
			</transition>
		</template>
	</div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
export default {
	props: {
		showSection: Object,
		auth: {
			default() {
				return {
					env: '',
					urls: {},
					myInfo: {},
					isLogin: false,
				}
			},
		},
	},
	data() {
		return {
			show: false,
		}
	},
	components: {
		TheHeader,
	},
	methods: {
		loginService() {
			setTimeout(() => {
				this.show = false
			}, 2000)
		},
		mounted() {
			const redirectTarget = this.$route.query.continue
			// 자신으로 리다이렉트 제외
			if (
				redirectTarget === location.origin ||
				redirectTarget === location.origin + '/'
			) {
				location.href = '/'
				return false
			}

			// 로그인 필요 다이얼로그
			const needNotLogin =
				redirectTarget && redirectTarget.match(this.$urls['www'] !== null)
			if (redirectTarget && !needNotLogin) {
				this.show = true
				this.loginService()
			}
		},
	},
}
</script>

<style lang="scss" scoped></style>
