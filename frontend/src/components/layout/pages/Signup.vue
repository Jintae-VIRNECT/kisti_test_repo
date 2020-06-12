<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>{{ $t('signup.signupTitle') }}</h2>
				<p>{{ $t('signup.signupDesk') }}</p>
				<p class="input-title must-check">{{ $t('signup.account') }}</p>
				<el-input
					:placeholder="$t('signup.mailPlaceholder')"
					v-model="signup.email"
					type="email"
					clearable
					:disabled="authLoading"
					name="email"
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
					<span>{{ $t('signup.authentication.mail') }}</span>
				</el-button>

				<el-input
					:placeholder="$t('signup.authentication.number')"
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
					{{ $t('signup.authentication.reSubmit') }}
				</button>
				<p class="input-title must-check">
					{{ $t('signup.password.pass') }}
				</p>
				<el-input
					:placeholder="$t('signup.password.comfirm')"
					v-model="signup.password"
					show-password
					name="password"
					v-validate="'required|min:6|max:40'"
					:class="{ 'input-danger': errors.has('password') }"
				>
				</el-input>
				<el-input
					:placeholder="$t('signup.password.reComfirm')"
					v-model="passwordConfirm"
					show-password
					name="passwordConfirm"
					v-validate="'required|min:6|max:40'"
					:class="{ 'input-danger': signup.password !== passwordConfirm }"
				>
				</el-input>
				<p class="restriction-text">
					{{ $t('signup.password.notice') }}
				</p>

				<p class="input-title must-check">{{ $t('signup.name.name') }}</p>
				<el-input
					class="lastname-input"
					:placeholder="$t('signup.name.last')"
					clearable
					name="lastname"
					v-validate="'required'"
					v-model="signup.lastName"
				></el-input>
				<el-input
					class="firstname-input"
					:placeholder="$t('signup.name.first')"
					clearable
					name="firstname"
					v-validate="'required'"
					v-model="signup.firstName"
				></el-input>

				<p class="input-title must-check">{{ $t('signup.birth.birth') }}</p>
				<el-date-picker
					class="birth-input year-input"
					v-model="birth.year"
					type="year"
					name="birtnY"
					maxlength="4"
					:data-placeholder="$t('signup.birth.year')"
					v-validate="'required'"
					:clearable="false"
					@change="birth.month = birth.year"
				></el-date-picker>

				<el-date-picker
					class="birth-input month-input"
					v-model="birth.month"
					type="month"
					name="birthM"
					format="MM"
					maxlength="2"
					:data-placeholder="$t('signup.birth.month')"
					v-validate="'required'"
					:clearable="false"
					@change="birth.date = birth.month"
				></el-date-picker>

				<el-date-picker
					class="birth-input date-input"
					v-model="birth.date"
					type="date"
					name="birthD"
					format="dd"
					maxlength="2"
					:data-placeholder="$t('signup.birth.date')"
					v-validate="'required'"
					:clearable="false"
				></el-date-picker>

				<p class="input-title must-check">{{ $t('signup.route.title') }}</p>
				<el-select
					v-model="joinInfo"
					:placeholder="$t('signup.route.select')"
					name="joinInfo"
					@change="resetJoinInfo"
				>
					<el-option
						v-for="item in $t('signup.subscriptionPathLists')"
						:key="item"
						:label="item"
						:value="item"
					>
					</el-option>
				</el-select>
				<el-input
					:placeholder="$t('signup.route.placeholder')"
					v-if="joinInfo === $t('signup.route.other')"
					v-model="signup.joinInfo"
					type="text"
					name="email"
					clearable
				>
				</el-input>

				<p class="input-title must-check">
					{{ $t('signup.serviceInfo.title') }}
				</p>
				<el-select
					v-model="serviceInfo"
					:placeholder="$t('signup.serviceInfo.select')"
					name="serviceInfo"
					@change="resetServiceInfo"
				>
					<el-option
						v-for="item in $t('signup.serviceInfoLists')"
						:key="item"
						:label="item"
						:value="item"
					>
					</el-option>
				</el-select>
				<el-input
					:placeholder="$t('signup.serviceInfo.placeholder')"
					v-if="serviceInfo === $t('signup.serviceInfo.other')"
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
					@click="checkAge()"
					>{{ $t('signup.next') }}</el-button
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
			verificationText: this.$t('signup.authentication.verification'),
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
		async checkAge() {
			let today = dayjs()
			let userAge = dayjs(this.userBirth)
			try {
				let age = (await today.format('YYYY')) - userAge.format('YYYY')
				if (age > 14) {
					this.checkSignup()
					return true
				} else throw false
			} catch (e) {
				this.alertMessage(
					this.$t('signup.errors.ageLimit.title'),
					this.$t('signup.errors.ageLimit.contents'),
					'error',
				)
			}
		},
		async checkSignup() {
			// 폼내용전송
			try {
				const res = await this.passValidate(this.signup.password)
				if (res) {
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
					// console.log(this.signup)
					if (this.signup) {
						setTimeout(() => {
							window.scrollTo({
								left: 0,
								top: 0,
							})
						}, 400)
						this.$router.push({
							name: 'user',
							params: { signup: this.signup },
						})
					}
				} else throw res
			} catch (e) {
				this.alertMessage(
					this.$t('signup.errors.password.title'),
					this.$t('signup.errors.password.contents'),
					'error',
				)
			}
		},
		async sendEmail() {
			try {
				// const validEmail = await this.$validator.validate('email')

				const email = this.signup.email
				const mailAuth = await AuthService.emailAuth(email)

				// console.log(mailAuth)
				if (mailAuth.code == 200) {
					this.authLoading = true
					this.isVeritication = true
					this.delayResend()
					this.alertMessage(
						this.$t('signup.done.verification.title'), // 이메일 인증 메일 전송 성공
						this.$t('signup.done.verification.contents'), // 입력하신 이메일로 인증 메일을 전송했습니다. 인증 메일의 인증 번호를 확인하여 입력해 주세요.
						'success',
					)
				} else throw mailAuth
			} catch (e) {
				if (e.code === 2200)
					return this.alertMessage(
						this.$t('signup.errors.duplicateEmail.title'), // 이메일 인증 메일 전송 실패
						this.$t('signup.errors.duplicateEmail.contents'), // 이미 VIRNECT 회원으로 등록된 이메일 주소입니다.
						'error',
					)
				else
					return this.alertMessage(
						this.$t('signup.errors.verification.title'), // 이메일 인증 메일 전송 실패
						this.$t('signup.errors.verification.contents'), // 인증 메일 전송에 실패하였습니다. 잠시 후 다시 시도해 주세요.
						'error',
					)
			}
		},
		resendEmail() {
			if (this.setCount) {
				this.sendEmail()
			}
		},
		async checkVerificationCode() {
			if (this.verificationCode) {
				const code = {
					code: this.verificationCode,
					email: this.signup.email,
				}
				try {
					const res = await AuthService.verification(code)
					if (res.code === 200) {
						// console.log(res)
						this.signup.sessionCode = res.data.sessionCode
						this.isVeritication = false
						this.isValidEmail = false
						this.verificationText = this.$t(
							'signup.authentication.done', // 인증 완료
						)
						this.check.isEmail = true
						this.alertMessage(
							this.$t('signup.authentication.message.done.title'), // 이메일 인증 성공
							this.$t('signup.authentication.message.done.contents'), // 이메일 인증이 완료되었습니다.
							'success',
						)
					} else throw res
				} catch (e) {
					if (e.code === 2201)
						return this.alertMessage(
							this.$t('signup.authentication.message.errors.title'), //인증 번호 불일치
							this.$t('signup.authentication.message.errors.contents'), // 인증 번호가 일치하지 않습니다. 다시 확인하여 입력해 주세요.
							'error',
						)
					else
						return this.alertMessage(
							this.$t('signup.authentication.message.errors.fail'), // 이메일 인증 실패
							this.$t('signup.authentication.message.errors.failContents'), // 이메일 인증에 실패하였습니다. 잠시 후 다시 시도해 주세요.
							'error',
						)
				}
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
