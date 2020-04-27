<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>계정정보 찾기</h2>
				<p>
					회원 정보에 등록된 계정 복구 정보를 <br />이용하여 계정 정보를
					찾습니다.
				</p>

				<section class="find-wrap">
					<div class="find-head">
						<button :class="{ active: tab == 'email' }" @click="tab = 'email'">
							<span>이메일 찾기</span>
						</button>
						<button
							:class="{ active: tab == 'resetPassword' }"
							@click="tab = 'resetPassword'"
						>
							<span>비밀번호 재설정</span>
						</button>
					</div>
					<div class="find-body" v-if="tab == 'email'">
						<p class="info-text">
							{{ mailFindText }}
						</p>

						<div v-if="isFindEmail === null">
							<el-radio v-model="tabCategory" label="1"
								>회원 정보에 등록된 휴대폰 번호로 찾기</el-radio
							>
							<template v-if="tabCategory == '1'">
								<p class="input-title">이름</p>
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
								<p class="input-title">휴대폰 번호</p>
								<el-select
									v-model="findEmail.countryCode"
									placeholder="+82"
									v-validate="'required'"
									class="countrycode-input"
									name="countryCode"
								>
									<el-option
										v-for="item in countryCodeLists"
										:key="item.value"
										:label="item.label"
										:value="item.value"
									>
									</el-option>
								</el-select>
								<el-input
									placeholder="휴대폰 번호를 입력해 주세요"
									class="phonenumber-input"
									clearable
									name="phoneNumber"
									v-validate="'required'"
									v-model="findEmail.phoneNumber"
								></el-input>
							</template>

							<el-radio v-model="tabCategory" label="2"
								>복구 이메일로 찾기</el-radio
							>

							<template v-if="tabCategory == '2'">
								<p class="input-title">이름</p>
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
								<p class="input-title">복구 이메일 주소</p>
								<el-input
									placeholder="이메일 주소를 입력해 주세요"
									clearable
									name="recoveryEmail"
									v-validate="'required|email|max:50'"
									v-model="findEmail.recoveryEmail"
								></el-input>
							</template>
							<el-button
								class="next-btn block-btn"
								type="primary"
								@click="mailAccountFind"
								:disabled="emailFindActive"
								>이메일 찾기</el-button
							>
						</div>
						<div v-else class="mailfind-before">
							<div class="user-email-holder">
								<p>
									<i>이메일:</i> <span>{{ findUserData.email }}</span>
								</p>
								<p>
									<i>가입일:</i> <span>{{ findUserData.signUpDate }}</span>
								</p>
							</div>
							<el-button
								class="next-btn block-btn"
								type="primary"
								@click="$router.push({ name: 'login' })"
								>로그인</el-button
							>
						</div>
					</div>
					<div class="find-body" v-if="tab == 'resetPassword'">
						<div v-if="isCodeAuth === null">
							<p class="info-text">
								회원 가입 시 입력한 등록된 이메일 주소로 비밀번호 재설정 보안
								코드를 발송해 드립니다.
							</p>

							<p class="input-title">이메일</p>
							<el-input
								placeholder="이메일 주소를 입력해 주세요"
								clearable
								type="email"
								v-model="resetPass.email"
							></el-input>
							<el-button
								class="next-btn block-btn"
								type="primary"
								@click="emailPassCode"
								:disabled="!emailValid"
								>보안코드 전송</el-button
							>
						</div>
						<div v-else class="auth-before">
							<p class="info-text">
								{{ authText }}
							</p>
							<div class="user-email-holder">
								<p>
									<i>이메일:</i> <span>{{ resetPass.email }}</span>
								</p>
							</div>

							<el-input
								placeholder="이메일로 전송된 보안코드 입력"
								type="text"
								v-model="resetPass.authCode"
								maxlength="6"
								v-if="isCodeAuth === false"
							></el-input>

							<p class="input-title must-check" v-if="isCodeAuth === true">
								비밀번호
							</p>
							<el-input
								placeholder="새 비밀번호 입력"
								type="password"
								name="password"
								show-password
								v-model="resetPass.password"
								v-if="isCodeAuth === true"
								v-validate="'required|min:6|max:40'"
								:class="{ 'input-danger': errors.has('password') }"
							></el-input>
							<el-input
								placeholder="새 비밀번호 재입력"
								type="password"
								v-model="resetPass.comfirmPassword"
								v-if="isCodeAuth === true"
								show-password
								name="passwordConfirm"
								v-validate="'required|min:6|max:40'"
								:class="{
									'input-danger':
										resetPass.password !== resetPass.comfirmPassword,
								}"
							></el-input>
							<p class="restriction-text" v-if="isCodeAuth === true">
								비밀번호를 영문 대소문자, 숫자, 특수문자(.!@#$%)를 혼합하여
								8~20자로 입력해 주세요.
							</p>

							<el-button
								class="next-btn block-btn"
								type="primary"
								:disabled="resetPass.authCode.length < 6"
								@click="authCodeCheck"
								v-if="isCodeAuth === false"
								>보안코드 인증</el-button
							>
							<el-button
								class="next-btn block-btn"
								type="primary"
								:disabled="
									resetPass.password !== resetPass.comfirmPassword ||
										resetPass.password.length < 8
								"
								@click="checkPass"
								v-if="isCodeAuth === true"
								>비밀번호 변경</el-button
							>
						</div>
					</div>
				</section>
				<el-button class="inquiry-btn" v-if="false"
					><i class="el-icon-warning-outline"></i>문의하기</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import mixin from 'mixins/mixin'
import UserService from 'service/user-service'
import CountryCode from 'model/countryCode'
export default {
	mixins: [mixin],
	props: {
		findCategory: String,
	},
	data() {
		return {
			tab: 'email',
			tabCategory: '1',
			fullName: {
				firstName: '',
				lastName: '',
			},
			findEmail: {
				countryCode: '',
				phoneNumber: '',
				recoveryEmail: '',
			},
			resetPass: {
				email: '',
				authCode: '',
				password: '',
				comfirmPassword: '',
			},
			userId: null,
			userEmail: null,
			isFindEmail: null,
			isCodeAuth: null,
			findUserData: {
				email: null,
				signUpDate: null,
			},
			countryCodeLists: CountryCode.countryCode,
		}
	},
	mounted() {
		if (this.findCategory == 'email') return (this.tab = 'email')
		else return (this.tab = 'resetPassword')
	},
	watch: {
		tabCategory() {
			// console.log(this.tabCategory)
			this.findEmail.phoneNumber = ''
			this.findEmail.recoveryEmail = ''
		},
	},
	computed: {
		emailFindActive() {
			let val
			if (this.tabCategory == 1) {
				if (
					this.fullName.firstName == '' ||
					this.fullName.lastName == '' ||
					this.findEmail.phoneNumber == ''
				)
					return (val = true)
				else return (val = false)
			} else {
				if (
					this.fullName.firstName == '' ||
					this.fullName.lastName == '' ||
					this.findEmail.recoveryEmail == ''
				)
					return (val = true)
				else return (val = false)
			}
			return val
		},
		emailValid() {
			const checkEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/
			if (!checkEmail.test(this.resetPass.email)) return false
			else return true
		},
		mailFindText() {
			if (this.isFindEmail)
				return '입력하신 정보와 일치하는 회원의 정보입니다. 정보 보호를 위해 앞 4자리만 보여집니다.'
			else
				return '회원 정보에 등록된 사용자 정보로 이메일을 찾습니다. 원하는 방법을 선택해 주세요.'
		},
		authText() {
			if (this.isCodeAuth)
				return '새 비밀번호를 설정합니다. 비밀번호 변경 시, 기존 로그인된 디바이스에서 로그인이 해지됩니다.'
			else return '보안 코드 6자리를 입력해 주세요.'
		},
		mobileSet() {
			if (
				this.findEmail.countryCode === '' ||
				this.findEmail.phoneNumber === ''
			)
				return ''
			else return `${this.findEmail.countryCode}-${this.findEmail.phoneNumber}`
		},
	},
	methods: {
		async mailAccountFind() {
			try {
				const res = await UserService.userFindEmail({
					firstName: this.fullName.firstName,
					lastName: this.fullName.lastName,
					mobile: this.mobileSet,
					recoveryEmail: this.findEmail.recoveryEmail,
				})
				if (res.code === 200) {
					this.isFindEmail = true
					this.findUserData = res.data
				} else throw res
			} catch (e) {
				// console.log(e)
				if (e.code === 4002)
					return this.alertMessage(
						'계정정보 불일치',
						'입력하신 정보와 일치하는 VIRNECT 회원이 없습니다.',
						'error',
					)
				else
					return this.alertMessage(
						'계정정보 전송 오류',
						'계정정보 전송에 실패하였습니다. 잠시 후 다시 이용해 주세요.',
						'error',
					)
			}
		},
		async changePass() {
			const userInfo = {
				uuid: this.userId,
				email: this.userEmail,
				password: this.resetPass.password,
			}
			try {
				const res = await UserService.userPassChange(userInfo)
				console.log(res)
				if (res.code === 200)
					return this.confirmWindow(
						'비밀번호 변경 완료 ',
						'기존 로그인된 기기에서 로그아웃 됩니다. 변경된 새 비밀번호로 다시 로그인해 주세요.',
						'확인',
					)
				else throw res
			} catch (e) {
				if (e.code === 4009)
					return this.alertMessage(
						'비밀번호 재설정 실패',
						'이전과 동일한 비밀번호는 새 비밀번호로 설정할 수 없습니다.',
						'error',
					)
				else
					return this.alertMessage(
						'비밀번호 재설정 실패',
						'비밀번호 변경에 실패하였습니다. 잠시 후 다시 이용해 주세요.',
						'error',
					)
			}
		},
		async emailPassCode() {
			try {
				let res = await UserService.userPass({ email: this.resetPass.email })
				if (res.code === 200) {
					this.alertMessage(
						'보안코드 전송',
						'가입하신 이메일로 보안 코드가 전송되었습니다. 이메일의 보안 코드를 확인하여 입력해 주세요.',
						'success',
					)
					this.isCodeAuth = false
				} else {
					throw res
				}
			} catch (e) {
				// console.log(e.code)
				if (e.code === 4002)
					return this.alertMessage(
						'보안코드 전송 오류',
						'입력하신 정보와 일치하는 VIRNECT 회원이 없습니다.',
						'error',
					)
				else if (e.code === 9999)
					return this.alertMessage(
						'보안코드 전송 오류',
						'보안 코드는 보안 유지를 위해 발급 후 1분 내에 재발송 되지 않습니다. 계정의 메일함을 확인해 주시거나, 잠시 후 다시 이용해 주세요',
						'error',
					)
				else
					return this.alertMessage(
						'보안코드 전송 오류',
						'보안코드 이메일 전송에 실패하였습니다. 잠시 후 다시 이용해 주세요.',
						'error',
					)
			}
		},
		async authCodeCheck() {
			try {
				let res = await UserService.userCodeCheck({
					code: this.resetPass.authCode,
					email: this.resetPass.email,
				})
				if (res.code === 200) {
					this.userId = res.data.uuid
					this.userEmail = res.data.email
					this.alertMessage(
						'보안코드 일치',
						'보안 코드 인증이 완료되었습니다.',
						'success',
					)
					this.isCodeAuth = true
				} else {
					throw res
				}
			} catch (e) {
				// console.log(e.code)
				if (e.code === 4007)
					return this.alertMessage(
						'보안코드 불일치 오류',
						'입력하신 보안 코드가 일치하지 않습니다. 다시 한 번 확인해 주세요.',
						'error',
					)
				else
					return this.alertMessage(
						'보안코드 인증 오류',
						'보안 코드 인증에 실패하였습니다. 잠시 후 다시 이용해 주세요.',
						'error',
					)
			}
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
		async checkPass() {
			try {
				const res = await this.passValidate(this.resetPass.password)
				if (res) {
					this.changePass()
				} else throw res
			} catch (e) {
				this.alertMessage(
					'비밀번호 입력 오류',
					'비밀번호는 8~20자 이내로 영문 대,소문자/숫자/특수문자( . , !, @, #, $, % )를 3가지 이상 조합하여 입력해 주세요. 연속된 숫자 또는 4자 이상의 동일 문자는 비밀번호로 사용할 수 없습니다.',
					'error',
				)
			}
		},
	},
}
</script>

<style lang="scss" scoped>
.row-bg > div {
	width: 460px;
	font-weight: 500;
}
.find-wrap {
	margin-top: 52px;
	border: 1px solid #eaedf3;
	border-radius: 4px;
}
.find-head {
	font-size: 0;
	border-bottom: 1px solid #eaedf3;
	button {
		width: 50%;
		height: 60px;
		color: #0d2a58;
		font-size: 16px;
		opacity: 0.6;
		&.active {
			opacity: 1;
		}
	}
	.active span {
		position: relative;
		&:after {
			position: absolute;
			bottom: -21px;
			left: 0;
			width: 100%;
			height: 3px;
			background: #1468e2;
			content: '';
		}
	}
}
.el-radio {
	margin-top: 36px;
}
.find-body {
	padding-top: 44px;
	padding-right: 40px;
	padding-bottom: 52px;
	padding-left: 40px;
	font-size: 16px;
	text-align: left;
	.info-text {
		margin-bottom: 4px;
		color: #103573;
		+ .input-title {
			margin-top: 40px;
		}
	}
}
.inquiry-btn {
	color: #6f7681;
	i {
		margin-right: 6px;
		padding-bottom: 3px;
		font-weight: bold;
		font-size: 18px;
		vertical-align: middle;
	}
}
.el-button.next-btn {
	margin-top: 52px;
}
.input-title {
	margin-top: 16px;
}

.user-email-holder {
	position: relative;
	margin: 32px 0 24px;
	padding: 26px 28px;
	background: #f2f4f7;
	p + p {
		margin-top: 16px;
	}
}
.auth-before,
.mailfind-before {
	.info-text {
		color: #103573;
	}
	i {
		position: absolute;
		left: 28px;
		color: #8b96ac;
	}
	span {
		display: block;
		padding-left: 100px;
		// text-align: center;
	}
}
</style>
