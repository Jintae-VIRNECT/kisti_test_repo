<template>
	<el-row type="flex" justify="center" align="middle" class="row-bg">
		<el-col>
			<h2>로그인</h2>
			<p class="input-title">이메일</p>
			<el-input
				placeholder="이메일을 입력해 주세요"
				v-model="login.email"
				:class="'loginAlert' ? errors.has('email') : ''"
				class="id-input"
				clearable
			>
			</el-input>

			<p class="input-title">비밀번호</p>
			<el-input
				placeholder="비밀번호를 입력해 주세요"
				v-model="login.password"
				clearable
				:class="'loginAlert' ? errors.has('password') : ''"
				class="pass-input"
				type="primary"
			></el-input>
			<p class="login-alert-msg" v-if="errors.has('email')">
				아이디 또는 비밀번호를 다시 확인해주세요.
			</p>
			<p class="login-alert-msg" v-if="errors.has('password')">
				아이디 또는 비밀번호를 다시 확인해주세요.
			</p>
			<div class="alert alert-danger" role="alert" v-if="message">
				{{ message }}
			</div>

			<div class="checkbox-wrap">
				<el-checkbox v-model="login.rememberMe">이메일 저장</el-checkbox>
				<el-checkbox v-model="login.autoLogin">자동 로그인</el-checkbox>
			</div>

			<el-button type="primary" @click="handleLogin" :disabled="loading"
				>로그인</el-button
			>
			<div class="find-wrap">
				<router-link to="/find">이메일 찾기</router-link>
				<router-link to="/find">비밀번호 재설정</router-link>
				<router-link to="/register">회원가입</router-link>
			</div>
		</el-col>
	</el-row>
	<!-- <div class="col-md-12">
		<div class="card card-container">
			<img
				id="profile-img"
				src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
				class="profile-img-card"
			/>
			<form name="form" @submit.prevent="handleLogin">
				<div class="form-group">
					<label for="email">이메일</label>
					<input
						id="email"
						type="text"
						class="form-control"
						name="email"
						v-model="login.email"
						v-validate="'required'"
					/>
					<div
						class="alert alert-danger"
						role="alert"
						v-if="errors.has('email')"
					>
						이메일을 입력해주세요
					</div>
				</div>
				<div class="form-group">
					<label for="password">비밀번호</label>
					<input
						id="password"
						type="password"
						class="form-control"
						name="password"
						v-model="login.password"
						v-validate="'required'"
					/>
					<div
						class="alert alert-danger"
						role="alert"
						v-if="errors.has('password')"
					>
						비밀번호를 입력해주세요
					</div>
				</div>
				<div class="form-group">
					<button class="btn btn-primary btn-block" :disabled="loading">
						<span
							class="spinner-border spinner-border-sm"
							v-show="loading"
						></span>
						<span>로그인</span>
					</button>
				</div>
				<div class="form-group">
					<div class="custom-control custom-checkbox">
						<input
							id="remember-me"
							type="checkbox"
							class="custom-control-input"
							name="rememberMe"
							v-model="login.rememberMe"
						/>
						<label for="remember-me" class="custom-control-label"
							>자동 로그인</label
						>
					</div>
				</div>
				<div class="form-group">
					<div class="alert alert-danger" role="alert" v-if="message">
						{{ message }}
					</div>
				</div>
			</form>
			<div class="mt-4">
				<div class="d-flex justify-content-center links">
					<a href="/register" class="ml-2">회원가입</a>
				</div>
				<div class="d-flex justify-content-center links">
					<a href="/find">비밀번호 찾기</a>
				</div>
			</div>
		</div>
	</div> -->
</template>

<script>
import Login from '../model/login'

export default {
	name: 'login',
	computed: {
		loggedIn() {
			return this.$store.state.auth.status.loggedIn
		},
	},
	data() {
		return {
			login: new Login('smic1', 'smic1234'),
			loading: false,
			message: '',
		}
	},
	mounted() {
		if (this.loggedIn) {
			this.$router.push('/profile')
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
				this.$store.dispatch('auth/login', this.login).then(
					() => {
						this.$router.push('/profile')
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

.el-col {
	height: 510px;
	// border: solid 1px rgba(216, 216, 216, 0.7);
	background: #fff;
	// padding: 75px 80px 0;
	border-radius: 6px;
}

.el-input {
	margin: 5px 0;
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
