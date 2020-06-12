<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>{{ $t('login.title') }}</h2>
				<p class="input-title">{{ $t('login.email') }}</p>
				<el-input
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
import Login from 'model/login'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'

import footerSection from '../common/Footer'
import auth from 'WC-Modules/javascript/api/virnectPlatform/virnectPlatformAuth'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

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
			isLogin: null,
			message: '',
			token: Cookies.get('accessToken'),
			rememberEmail: localStorage.getItem('email'),
			rememberLogin: localStorage.getItem('auto'),
		}
	},
	beforeMount() {
		this.checkToken()
	},
	mounted() {
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
			let res = await auth.init()
			this.isLogin = res.isLogin
			if (this.isLogin === true && this.login.autoLogin === true) {
				let redirectTarget = this.$route.query.continue
				if (redirectTarget) {
					location.href = /^https?:/.test(redirectTarget)
						? redirectTarget
						: `//${redirectTarget}`
				} else {
					location.href = urls.workstation[process.env.TARGET_ENV]
				}
			}
		},
		emailRemember(email, check) {
			if (check == true) {
				this.rememberEmail = true
				localStorage.setItem('email', email)
			} else {
				localStorage.removeItem('email')
			}
		},
		autoLogin(check) {
			if (check == true) {
				this.rememberLogin = true
				localStorage.setItem('auto', check)
			} else {
				localStorage.removeItem('auto')
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
			if (this.errors.any()) {
				this.loading = false
				return
			}
			if (this.login.email && this.login.password) {
				new Login(this.login.email, this.login.password)
				try {
					const res = await AuthService.login(this.login)
					let redirectTarget = this.$route.query.continue
					if (redirectTarget) {
						location.href = /^https?:/.test(redirectTarget)
							? redirectTarget
							: `//${redirectTarget}`
					} else {
						location.href = urls.workstation[process.env.TARGET_ENV]
					}
					// location.href = urls.workstation[process.env.TARGET_ENV]
				} catch (e) {
					if (e.code === 2000) {
						this.alertWindow(
							this.$t('login.accountError.title'),
							this.$t('login.accountError.contents'),
							this.$t('login.accountError.btn'),
						)
						this.loading = false
						this.message = e.message
					} else {
						this.alertMessage(
							this.$t('login.networkError.title'),
							this.$t('login.networkError.contents'),
							'error',
						)
						this.loading = false
					}
				}
			}
		},
	},
}
</script>

<style lang="scss" scoped>
h1 {
	margin-right: -2px;
	margin-bottom: 39px;
	margin-left: -2px;
}

.el-button {
	margin-top: 30px;
}
</style>
