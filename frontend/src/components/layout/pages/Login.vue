<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>로그인</h2>
				<p class="input-title">이메일</p>
				<el-input
					placeholder="이메일을 입력해 주세요"
					v-model="login.email"
					name="email"
					:class="{ 'input-danger': message }"
					clearable
					v-validate="'required'"
				>
				</el-input>

				<p class="input-title">비밀번호</p>
				<el-input
					placeholder="비밀번호를 입력해 주세요"
					v-model="login.password"
					show-password
					name="password"
					:class="{ 'input-danger': message }"
					v-validate="'required'"
					@keydown="handleLogin"
				></el-input>

				<p class="warning-msg danger-color" v-if="message">{{ message }}</p>

				<div class="checkbox-wrap">
					<el-checkbox
						v-model="login.rememberMe"
						@change="emailRemember(login.email, login.rememberMe)"
						>이메일 저장</el-checkbox
					>
					<el-checkbox
						@change="autoLogin(login.autoLogin)"
						v-model="login.autoLogin"
						>자동 로그인</el-checkbox
					>
				</div>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="handleLogin"
					@keydown.enter="handleLogin"
					:disabled="loading || login.email == '' || login.password == ''"
					>로그인</el-button
				>
				<div class="find-wrap">
					<router-link :to="{ name: 'find', params: { findCategory: 'email' } }"
						>이메일 찾기</router-link
					>
					<router-link
						:to="{ name: 'find', params: { findCategory: 'resetPassword' } }"
						>비밀번호 재설정</router-link
					>
					<router-link to="/terms">회원가입</router-link>
				</div>
			</el-col>
		</el-row>
		<footer-section></footer-section>
	</div>
</template>

<script>
import Login from 'model/login'

import footerSection from '../common/Footer'

export default {
	name: 'login',
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
			rememberEmail: localStorage.getItem('email'),
			rememberLogin: localStorage.getItem('auto'),
		}
	},
	mounted() {
		if (this.rememberLogin) {
			this.login.autoLogin = true
			if (this.loggedIn) return this.$router.push({ name: 'profile' })
		}
		if (this.rememberEmail) {
			this.login.rememberMe = true
			this.login.email = this.rememberEmail
		}
	},
	methods: {
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
		alertWindow(msg) {
			this.$alert(msg, '계정 정보 입력 오류', {
				confirmButtonText: '확인',
			})
		},
		handleLogin() {
			this.loading = true
			this.$validator.validateAll()
			if (this.errors.any()) {
				this.loading = false
				return
			}
			if (this.login.email && this.login.password) {
				new Login(this.login.email, this.login.password)
				this.$store.dispatch('auth/login', this.login).then(
					() => {
						let redirectTarget = this.$route.query.continue
						if (redirectTarget) {
							location.href = /^https?:/.test(redirectTarget)
								? redirectTarget
								: `//${redirectTarget}`
						} else {
							location.href = '//virnect.com'
						}
					},
					error => {
						this.loading = false
						this.message = error
						this.alertWindow(error)
					},
				)
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
