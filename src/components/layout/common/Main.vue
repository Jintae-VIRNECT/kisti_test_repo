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
			<TheHeader :showSection="showSection" :runtimeInfo="runtimeInfo">
				<template slot="subTitle">{{ $t('login.subTitle') }}</template>
			</TheHeader>
			<transition name="app-fade" mode="out-in">
				<router-view />
			</transition>
		</template>
	</div>
</template>

<script>
import Vue from 'vue'
import api from 'api/axios'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
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
			show: false,
			showSection: {
				login: false,
				language: true,
			},
			runtimeInfo: {
				urls: null,
				env: null,
			},
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
			redirectTarget && redirectTarget.match(window.urls['www'] !== null)
		if (redirectTarget && !needNotLogin) {
			this.show = true
			this.loginService()
		}
	},
}
</script>

<style lang="scss" scoped></style>
