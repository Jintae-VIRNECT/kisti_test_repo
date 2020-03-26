<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>로그인</h2>
				<p class="input-title">이메일</p>
				<el-input
					placeholder="이메일을 입력해 주세요"
					v-model="login.email"
					:class="{'input-danger' : message}"
					clearable
				>
				</el-input>

				<p class="input-title">비밀번호</p>
				<el-input
					placeholder="비밀번호를 입력해 주세요"
					v-model="login.password"
					clearable
					:class="{'input-danger' : message}"
				></el-input>
				
				<p class="warning-msg danger-color" v-if="message">{{message}}</p>

				<div class="checkbox-wrap">
					<el-checkbox v-model="login.rememberMe">이메일 저장</el-checkbox>
					<el-checkbox v-model="login.autoLogin">자동 로그인</el-checkbox>
				</div>

				<el-button type="primary" @click="handleLogin" :disabled="loading || login.email == '' || login.password == ''"
					>로그인</el-button
				>
				<div class="find-wrap">
					<router-link to="/find">이메일 찾기</router-link>
					<router-link to="/find">비밀번호 재설정</router-link>
					<router-link to="/register">회원가입</router-link>
				</div>
			</el-col>
		</el-row>
		<footer-section></footer-section>
	</div>
</template>

<script>
import Login from '../model/login'

import footerSection from './layout/common/Footer'

export default {
	name: 'login',
	components: {
		footerSection
	},
	computed: {
		loggedIn() {
			return this.$store.state.auth.status.loggedIn
		},
	},
	data() {
		return {
			login: {
				email: '',
				password: ''
			},
			loading: false,
			message: ''
		}
	},
	mounted() {
		if (this.loggedIn) {
			this.$route.path = '/profile'
		}
	},
	methods: {
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
						this.$route.path = '/profile'
					},
					error => {
						this.loading = false
						this.message = error.message
					},
				)
			}
		},
	},
}
</script>

<style lang="scss" scoped>
.row-bg {
	height: 100%;
	text-align: center;
	> div {
		width: 380px;
	}
}

h1 {
	margin-left: -2px;
	margin-right: -2px;
	margin-bottom: 39px;
}
.el-button {
	margin-top: 30px;
	width: 100%;
	height: 50px;
}
.login-alert-msg {
	text-align: left;
	margin-top: 15px;
	font-size: 13px;
	color: #ff3d3d;
	img {
		vertical-align: middle;
		margin-right: 6px;
	}
}
</style>
