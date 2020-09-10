<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>{{ $t('login.title') }}</h2>
				<p class="input-title">{{ $t('login.email') }}</p>
				<el-input
					ref="focusOut"
					:placeholder="$t('login.emailPlaceholder')"
					v-model="login.email"
					name="email"
					:class="{ 'input-danger': message }"
					clearable
					v-validate="'required'"
				>
				</el-input>

				<p class="input-title">{{ $t('login.password') }}</p>
				<el-input
					:placeholder="$t('login.passwordPlaceholder')"
					v-model="login.password"
					show-password
					name="password"
					:class="{ 'input-danger': message }"
					v-validate="'required'"
					@keyup.enter.native="handleLogin"
				></el-input>

				<p class="warning-msg danger-color" v-if="message">{{ message }}</p>

				<div class="checkbox-wrap">
					<el-checkbox
						v-model="login.rememberMe"
						@change="emailRemember(login.email, login.rememberMe)"
						>{{ $t('login.remember') }}</el-checkbox
					>
					<el-checkbox
						@change="autoLogin(login.autoLogin)"
						v-model="login.autoLogin"
						>{{ $t('login.autoLogin') }}</el-checkbox
					>
				</div>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="handleLogin"
					:disabled="loading || login.email == '' || login.password == ''"
					>{{ $t('login.title') }}</el-button
				>
				<div class="find-wrap">
					<router-link
						:to="{ name: 'findTab', params: { findCategory: 'email' } }"
						>{{ $t('login.findAccount') }}</router-link
					>
					<router-link
						:to="{
							name: 'findTab',
							params: { findCategory: 'reset_password' },
						}"
						>{{ $t('login.resetPassword') }}</router-link
					>
					<router-link to="/terms">{{ $t('login.signUp') }}</router-link>
				</div>
			</el-col>
		</el-row>
		<footer-section></footer-section>
	</div>
</template>

<script>
import Cookies from 'js-cookie'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'

import footerSection from '../common/Footer'
import auth from 'api/virnectPlatformAuth'

export default {
	name: 'login',
	mixins: [mixin],
	components: {
		footerSection,
	},
	computed: {
		loggedIn() {
			return this.$store.state.auth.initial.status.loggedIn
		},
	},
	data() {
		return {
			login: {
				email: '',
				password: '',
				rememberMe: null,
				autoLogin: null,
			},
			loading: false,
			message: '',
			token: Cookies.get('accessToken'),
			rememberEmail: Cookies.get('email'),
			rememberLogin: Cookies.get('auto'),
			cookieOption: {
				domain:
					location.hostname.split('.').length === 3
						? location.hostname.replace(/.*?\./, '')
						: location.hostname,
			},
		}
	},
	beforeMount() {
		this.checkToken()
	},
	mounted() {
		// console.log(this.rememberEmail)
		if (this.rememberLogin === 'true') {
			this.login.autoLogin = true
		}

		if (this.rememberEmail) {
			this.login.rememberMe = true
			this.login.email = this.rememberEmail
		}
	},
	methods: {
		async checkToken() {
			const res = await auth.init()
			const redirectTarget = this.$route.query.continue
			if (!res.isLogin) return false

			if (redirectTarget) {
				location.href = /^https?:/.test(redirectTarget)
					? redirectTarget
					: `//${redirectTarget}`
			} else if (this.login.autoLogin) {
				location.href = window.urls['workstation']
			}
		},
		emailRemember(email, check) {
			if (check == true) {
				this.rememberEmail = true
				Cookies.set('email', email, this.cookieOption)
			} else {
				Cookies.remove('email', this.cookieOption)
			}
		},
		autoLogin(check) {
			if (check == true) {
				this.rememberLogin = true
				Cookies.set('auto', check, {
					domain:
						location.hostname.split('.').length === 3
							? location.hostname.replace(/.*?\./, '')
							: location.hostname,
				})
			} else {
				Cookies.remove('auto')
			}
		},
		alertWindow(title, msg, btn) {
			this.$alert(msg, title, {
				confirmButtonText: btn,
			})
		},
		async handleLogin() {
			this.loading = true
			this.$validator.validateAll()
			this.$refs.focusOut.focus()
			if (this.errors.any()) {
				this.loading = false
				return
			}
			try {
				let res = await AuthService.login({ params: this.login })
				let redirectTarget = this.$route.query.continue
				if (res.code === 200) {
					const cookieOption = {
						secure: true,
						sameSite: 'None',
						expires: res.data.expireIn / 3600000,
						domain:
							location.hostname.split('.').length === 3
								? location.hostname.replace(/.*?\./, '')
								: location.hostname,
					}
					Cookies.set('accessToken', res.data.accessToken, cookieOption)
					Cookies.set('refreshToken', res.data.refreshToken, cookieOption)
					if (redirectTarget) {
						location.href = /^https?:/.test(redirectTarget)
							? redirectTarget
							: `//${redirectTarget}`
					} else {
						location.href = window.urls['workstation']
					}
				} else throw res
			} catch (e) {
				if (e.code === 2000) {
					this.loading = false
					this.message = this.$t('login.accountError.contents')
				} else {
					this.alertMessage(
						this.$t('login.networkError.title'),
						this.$t('login.networkError.contents'),
						'error',
					)
					this.message = e.message
					this.loading = false
				}
			}
		},
	},
}
</script>

<style lang="scss" scoped>
@import '~assets/css/mixin.scss';

h1 {
	margin-right: -2px;
	margin-bottom: 39px;
	margin-left: -2px;
}

.el-button {
	margin-top: 30px;
}

@media (max-width: $mobile) {
	.el-button.next-btn {
		margin-top: 40px;
	}
}
</style>
