<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>회원 정보 등록</h2>
				<p>하나의 계정으로 VIRNECT 정체 제품을 이용할 수 있습니다.</p>
				<p class="input-title must-check">계정 이메일</p>
				<el-input
					placeholder="이메일을 입력해 주세요"
					v-model="signup.email"
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
					:disabled="errors.has('email') || signup.email == ''"
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
					v-model="signup.password"
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
					:class="{ 'input-danger': signup.password !== passwordConfirm }"
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
					v-model="signup.lastName"
				></el-input>
				<el-input
					class="firstname-input"
					placeholder="이름"
					clearable
					name="firstname"
					v-validate="'required'"
					v-model="signup.firstName"
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
					v-model="birth.date"
					type="date"
					name="birthD"
					format="dd"
					maxlength="2"
					v-validate="'required'"
					:clearable="false"
				></el-date-picker>

				<p class="input-title must-check">가입 경로</p>
				<el-select
					v-model="joinInfo"
					placeholder="가입 경로 선택"
					name="joinInfo"
					@change="resetJoinInfo()"
				>
					<el-option
						v-for="item in $t('signup.subscriptionPathLists')"
						:key="item.value"
						:label="item.label"
						:value="item.label"
					>
					</el-option>
				</el-select>
				<el-input
					placeholder="가입 경로를 입력해 주세요"
					v-if="joinInfo == '직접 입력'"
					v-model="signup.joinInfo"
					type="text"
					name="email"
					clearable
				>
				</el-input>

				<p class="input-title must-check">서비스 분야</p>
				<el-select
					v-model="serviceInfo"
					placeholder="서비스 분야 선택"
					name="serviceInfo"
					@change="resetServiceInfo()"
				>
					<el-option
						v-for="item in $t('signup.serviceInfoLists')"
						:key="item.value"
						:label="item.label"
						:value="item.label"
					>
					</el-option>
				</el-select>
				<el-input
					placeholder="서비스 분야 직접 입력"
					v-if="serviceInfo == '기타'"
					v-model="signup.serviceInfo"
					type="text"
					name="email"
					clearable
				>
				</el-input>

				<el-button
					class="next-btn block-btn"
					type="info"
					:disabled="!nextBtn"
					@click="checkSignup()"
					>다음</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import Signup from 'model/signup'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'
import dayjs from 'dayjs'

export default {
	name: 'signup',
	mixins: [mixin],
	computed: {
		loggedIn() {
			return this.$store.state.auth.status.loggedIn
		},
	},
	props: {
		marketInfoReceive: Boolean,
		policyAgree: Boolean,
	},
	data() {
		return {
			authLoading: false,
			isVeritication: false,
			verificationText: '인증 확인',
			signup: {
				email: '',
				password: '',
				firstName: '',
				lastName: '',
				birth: '',
				marketInfoReceive: false,
				joinInfo: '',
				serviceInfo: '',
				sessionCode: '',
			},
			passwordConfirm: '',
			fullName: {
				lastName: '',
				firstName: '',
			},
			birth: {
				year: '',
				month: '',
				date: '',
			},
			joinInfo: '',
			serviceInfo: '',
			submitted: false,
			successful: false,
			setCount: false,
			isValidEmail: false,
			verificationCode: '',
			message: '',
			check: {
				isEmail: false,
			},
		}
	},
	computed: {
		userName() {
			let fullName
			fullName = this.fullName.firstName + this.fullName.lastName
			this.signup.name = fullName
			return fullName
		},
		userBirth() {
			let birth,
				year = dayjs(this.birth.year),
				month = dayjs(this.birth.month + 1),
				date = dayjs(this.birth.date)
			birth = year.format('YYYY-') + month.format('MM-') + date.format('DD')

			this.signup.birth = birth
			return birth
		},
		joinInfoComp() {
			if (this.signup.joinInfo == '')
				return (this.signup.joinInfo = this.joinInfo)
		},
		serviceInfoComp() {
			if (this.signup.serviceInfo == '')
				return (this.signup.serviceInfo = this.serviceInfo)
		},
		nextBtn() {
			let val = true
			if (!this.check.isEmail) return (val = false)
			if (
				this.signup.password !== this.passwordConfirm &&
				this.passwordConfirm !== ''
			)
				return (val = false)
			if (this.signup.lastName == '' || this.signup.firstName == '')
				return (val = false)

			if (
				this.birth.year == '' ||
				this.birth.month == '' ||
				this.birth.date == ''
			)
				return (val = false)
			if (this.joinInfoComp == '') return (val = false)
			if (this.serviceInfoComp == '') return (val = false)
			return val
		},
	},
	mounted() {
		if (this.loggedIn || !this.policyAgree) {
			this.$router.push('/')
		}
		if (this.marketInfoReceive)
			return (this.signup.marketInfoReceive = 'ACCEPT')
		else return (this.signup.marketInfoReceive = 'REJECT')
	},
	methods: {
		checkSignup() {
			// 폼내용전송
			if (!this.passValidate(this.signup.password)) {
				this.alertMessage(
					'비밀번호 입력 오류',
					'비밀번호는 8~20자 이내로 영문 대,소문자/숫자/특수문자( . , !, @, #, $, % )를 3가지 이상 조합하여 입력해 주세요. 연속된 숫자 또는 4자 이상의 동일 문자는 비밀번호로 사용할 수 없습니다.',
					'error',
				)
			}

			this.message = ''
			this.submitted = true
			new Signup(
				this.signup.email,
				this.signup.password,
				this.signup.firstName,
				this.signup.lastName,
				this.userBirth,
				this.marketInfoReceive,
				this.joinInfoComp,
				this.serviceInfoComp,
				this.signup.sessionCode,
			)
			console.log(this.signup)
			if (this.signup) {
				this.$store.dispatch('auth/signup', this.signup).then(
					data => {
						if (data) {
							this.alertMessage('가입 완료', 'ㅁㄴㅇ.', 'success')
						}
					},
					error => {
						if (error) {
							this.alertMessage(
								'기타 오류',
								`회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.`,
								'error',
							)
						}
					},
				)
			}
		},
		async sendEmail() {
			const validEmail = await this.$validator.validate('email')
			if (!validEmail) {
				return this.alertMessage(
					'이메일 형식 오류',
					'이메일 형식이 바르지 않습니다. 올바른 이메일 주소를 입력해 주세요.',
					'error',
				)
			} else {
				const email = this.signup.email
				const result = AuthService.emailAuth(email)
				this.authLoading = true
				this.isVeritication = true
				this.delayResend()
				return this.alertMessage(
					'이메일 인증 메일 전송 성공',
					'입력하신 이메일로 인증 메일을 전송했습니다. 인증 메일의 인증 번호를 확인하여 입력해 주세요.',
					'success',
				)
			}
		},
		async resendEmail() {
			if (this.setCount) {
				const email = this.signup.email
				const result = AuthService.emailAuth(email)
				this.delayResend()
				return this.alertMessage(
					'이메일 인증 메일 재전송 성공',
					'입력하신 이메일로 인증 메일을 재전송했습니다. 인증 메일의 인증 번호를 확인하여 입력해 주세요.',
					'success',
				)
			}
		},
		checkVerificationCode() {
			const code = {
				code: this.verificationCode,
				email: this.signup.email,
			}
			if (this.verificationCode) {
				this.$store.dispatch('auth/verification', code).then(
					data => {
						if (data) {
							this.signup.sessionCode = data.sessionCode
							this.isVeritication = false
							this.isValidEmail = false
							this.verificationText = '인증 완료'
							this.check.isEmail = true
							this.alertMessage(
								'이메일 인증 성공',
								'이메일 인증이 완료되었습니다.',
								'success',
							)
						}
					},
					error => {
						if (error) {
							this.alertMessage(
								'인증 번호 불일치',
								'인증 번호가 일치하지 않습니다. 다시 확인하여 입력해 주세요.',
								'error',
							)
						}
					},
				)
			}
			this.delayResend()
		},
		passValidate(password) {
			let typeCount = 0
			if (/[0-9]/.test(password)) typeCount++
			if (/[a-z]/.test(password)) typeCount++
			if (/[A-Z]/.test(password)) typeCount++
			if (/[$.$,$!$@$#$$$%]/.test(password)) typeCount++
			if (typeCount < 3) return false
			if (!/^.{8,20}$/.test(password)) return false
			if (/(.)\1\1\1/.test(password)) return false
			if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password))
				return false
			if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password))
				return false
			return true
		},
		resetJoinInfo() {
			this.signup.joinInfo = ''
		},
		resetServiceInfo() {
			this.signup.serviceInfo = ''
		},
		delayResend() {
			this.setCount = false
			setTimeout(() => {
				this.setCount = true
			}, 10000)
		},
	},
}
</script>

<style lang="scss" scoped>
.el-button.next-btn {
	margin-top: 60px;
}
</style>
