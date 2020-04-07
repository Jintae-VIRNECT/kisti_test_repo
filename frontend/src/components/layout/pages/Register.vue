<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>회원 정보 등록</h2>
				<p>하나의 계정으로 VIRNECT 정체 제품을 이용할 수 있습니다.</p>
				<p class="input-title must-check">계정 이메일</p>
				<el-input
					placeholder="이메일을 입력해 주세요"
					v-model="register.email"
					type="email"
					name="email"
					clearable
					:disabled="authLoading"
					v-validate="'required|email|max:50'"
				>
				</el-input>
				<el-button
					class="block-btn"
					type="info"
					:disabled="this.register.email.length <= 8"
					v-if="!authLoading"
					@click="sendEmail()"
				>
					<span>인증 메일 전송</span>
				</el-button>

				<el-input
					placeholder="인증 번호 6자리를 입력해 주세요"
					v-if="isVeritication"
					v-model="verificationCode"
					type="text"
					name="verificationCode"
					clearable
					maxlength="6"
					v-validate="'required|max:6'"
				>
				</el-input>
				<el-button
					class="block-btn"
					type="info"
					:disabled="this.verificationCode.length !== 6 || !isVeritication"
					v-if="authLoading"
					@click="checkVerificationCode()"
				>
					<span>{{ verificationText }}</span>
				</el-button>

				<button
					class="resend-btn"
					v-if="isVeritication == true"
					:class="{ disabled: !setCount }"
					@click="resendEmail()"
				>
					인증 메일 재전송
				</button>
				<p class="input-title must-check">비밀번호</p>
				<el-input
					placeholder="비밀번호 입력해 주세요"
					v-model="register.password"
					show-password
					name="password"
					v-validate="'required|min:6|max:40'"
					:class="{ 'input-danger': errors.has('password') }"
				>
				</el-input>
				<el-input
					placeholder="비밀번호 재입력해 주세요"
					v-model="passwordConfirm"
					show-password
					name="passwordConfirm"
					v-validate="'required|min:6|max:40'"
					:class="{ 'input-danger': register.password !== passwordConfirm }"
				>
				</el-input>
				<p class="restriction-text">
					8~20자의 영문 대, 소문자, 숫자, 특수문자 중 3가지 이상을 조합하여
					입력해 주세요.
				</p>

				<p class="input-title must-check">이름</p>
				<el-input
					class="lastname-input"
					placeholder="성"
					clearable
					name="lastname"
					v-validate="'required'"
					v-model="fullName.lastName"
				></el-input>
				<el-input
					class="firstname-input"
					placeholder="이름"
					clearable
					name="firstname"
					v-validate="'required'"
					v-model="fullName.firstName"
				></el-input>

				<p class="input-title must-check">생년월일</p>
				<el-date-picker
					class="birth-input year-input"
					placeholder="년"
					v-model="birth.year"
					type="year"
					name="birtnY"
					maxlength="4"
					v-validate="'required'"
					:clearable="false"
				></el-date-picker>

				<el-date-picker
					class="birth-input"
					placeholder="월"
					v-model="birth.month"
					type="month"
					name="birthM"
					format="MM"
					maxlength="2"
					v-validate="'required'"
					:clearable="false"
				></el-date-picker>

				<el-date-picker
					class="birth-input"
					placeholder="일"
					v-model="birth.day"
					type="date"
					name="birthD"
					format="dd"
					maxlength="2"
					v-validate="'required'"
					:clearable="false"
				></el-date-picker>

				<p class="input-title must-check">가입 경로</p>
				<el-select
					v-model="register.joinInfo"
					placeholder="가입 경로 선택"
					name="joinInfo"
				>
					<el-option
						v-for="item in $t('register.subscriptionPathLists')"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					>
					</el-option>
				</el-select>

				<el-input
					placeholder="가입 경로를 입력해 주세요"
					v-if="register.joinInfo == 9"
					v-model="joinInfoETC"
					type="text"
					name="email"
					clearable
				>
				</el-input>

				<p class="input-title must-check">서비스 분야</p>
				<el-select
					v-model="register.serviceInfo"
					placeholder="서비스 분야 선택"
					name="serviceInfo"
				>
					<el-option
						v-for="item in $t('register.serviceInfoLists')"
						:key="item.value"
						:label="item.label"
						:value="item.value"
					>
					</el-option>
				</el-select>

				<el-input
					placeholder="서비스 분야 직접 입력"
					v-if="register.serviceInfo == 12"
					v-model="serviceInfoETC"
					type="text"
					name="email"
					clearable
				>
				</el-input>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="handleRegister()"
					>다음</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import Register from 'model/register'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'

export default {
	name: 'register',
	mixins: [mixin],
	computed: {
		loggedIn() {
			return this.$store.state.auth.status.loggedIn
		},
	},
	props: {
		marketInfoReceive: Boolean,
	},
	data() {
		return {
			authLoading: false,
			isVeritication: false,
			verificationText: '인증 확인',
			register: {
				email: '',
				password: '',
				name: '',
				birth: '',
				marketInfoReceive: '',
				joinInfo: '',
				serviceInfo: '',
				session: '',
			},
			passwordConfirm: '',
			fullName: {
				lastName: '',
				firstName: '',
			},
			birth: {
				year: '',
				month: '',
				day: '',
			},
			joinInfoETC: '',
			serviceInfoETC: '',
			submitted: false,
			successful: false,
			setCount: false,
			isValidEmail: false,
			verificationCode: '',
			message: '',
		}
	},
	computed: {
		userName() {
			let fullName
			fullName = this.fullName.firstName + this.fullName.lastName
			this.register.name = fullName
			return fullName
		},
		userBirth() {
			let birth
			birth = this.birth.year + this.birth.month + this.birth.day
			this.register.birth = birth
			return birth
		},
	},
	mounted() {
		if (this.loggedIn) {
			this.$router.push('/')
		}
		if (this.marketInfoReceive)
			return (this.register.marketInfoReceive = 'active')
		else return (this.register.marketInfoReceive = 'inactive')
	},
	methods: {
		handleRegister() {
			// console.log(this.userName)
			this.message = ''
			this.submitted = true
			new Register(
				this.register.email,
				this.register.password,
				this.userName,
				this.userBirth,
				this.register.registerInfo,
				this.register.serviceInfo,
				this.register.session,
			)
			console.log(this.register)
			this.$validator.validate().then(valid => {
				if (valid) {
					this.$store
						.dispatch('auth/register', this.register)
						.then(
							data => {
								this.message = data.message
								this.successful = true
							},
							error => {
								this.message = error.message
								this.successful = false
							},
						)
						.then
						// this.$router.push('/')
						()
				}
			})
		},
		sendEmail() {
			const email = this.register.email
			const result = AuthService.emailAuth(email)
			// this.$store.dispatch('auth/email', email).then(
			//   data => {
			//     console.log('됨?')
			//   },
			//   error => {
			//     console.log('안됨?')
			//   }
			// )
			this.authLoading = true
			this.isVeritication = true
			this.delayResend()

			this.alertMessage(
				'이메일 인증 메일 전송 성공',
				'입력하신 이메일로 인증 메일을 전송했습니다. 인증 메일의 인증 번호를 확인하여 입력해 주세요.',
				'success',
			)
		},
		delayResend() {
			this.setCount = false
			setTimeout(() => {
				this.setCount = true
			}, 10000)
		},
		resendEmail() {
			if (this.setCount) {
				console.log('재전송')
				this.delayResend()
			}
		},
		checkVerificationCode() {
			const code = {
				code: this.verificationCode,
				email: this.register.email,
			}
			if (this.verificationCode) {
				this.$store.dispatch('auth/verification', code).then(
					data => {
						if (data.code == 200) {
							this.isVeritication = false
							;(this.isValidEmail = false),
								(this.verificationText = '인증 완료')
						}
					},
					error => {
						console.log(error + '에러')
					},
				)
			}
			// const result = AuthService.verification(code, email)
			this.delayResend()
		},
	},
}
</script>

<style lang="scss" scoped>
.el-button.next-btn {
	margin-top: 60px;
}
</style>
