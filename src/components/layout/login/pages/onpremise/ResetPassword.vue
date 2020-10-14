<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>비밀번호 재설정</h2>
				<p>
					계정 ID와 비밀번호 찾기 질문/답변으로 <br />
					비밀번호를 재설정할 수 있습니다.
				</p>

				<div class="find-body" v-if="!isQuestionAuth">
					<p class="input-title must-check">ID</p>
					<el-input
						placeholder="ID를 입력해 주세요"
						clearable
						type="email"
						v-model="resetPass.email"
						@keyup.enter.native="checkAuth()"
					></el-input>

					<div class="step-warp" v-if="step === 'number'">
						<el-button
							class="next-btn block-btn"
							type="primary"
							:disabled="resetPass.email == ''"
							@click="checkAuth()"
							>다음</el-button
						>
					</div>
					<div class="step-warp" v-else>
						<p class="input-title must-check">
							비밀번호 찾기 질문/답변
						</p>
						<el-select
							v-model="question"
							placeholder="원하는 질문을 선택해주세요"
							name="question"
						>
							<el-option
								v-for="item in questionList"
								:key="item"
								:label="item"
								:value="item"
							>
							</el-option>
						</el-select>
						<el-input
							placeholder="답변을 입력해주세요"
							v-if="question !== ''"
							v-model="answer"
							type="text"
							name="email"
							clearable
							@keyup.enter.native="checkAnswer()"
						>
						</el-input>
						<el-button
							class="next-btn block-btn"
							type="primary"
							:disabled="answer == ''"
							@click="checkAnswer()"
							>다음</el-button
						>
					</div>
				</div>
				<div class="find-wrap" v-else>
					<div class="find-body">
						<p class="info-text">
							비밀번호 변경 완료 후, 변경된 비밀번호로 로그인해 주세요.
						</p>
						<div class="user-email-holder">
							<p>
								<i>ID:</i>
								<span>{{ resetPass.email }}</span>
							</p>
						</div>

						<p class="input-title must-check">
							비밀번호
						</p>
						<el-input
							placeholder="새 비밀번호를 입력해 주세요"
							type="password"
							name="password"
							show-password
							v-model="resetPass.password"
							v-validate="'required|min:6|max:40'"
							:class="{ 'input-danger': errors.has('password') }"
						></el-input>
						<el-input
							placeholder="새 비밀번호를 재입력해 주세요"
							type="password"
							v-model="resetPass.comfirmPassword"
							show-password
							name="passwordConfirm"
							v-validate="'required|min:6|max:40'"
							:class="{
								'input-danger':
									resetPass.password !== resetPass.comfirmPassword,
							}"
							@keyup.enter.native="checkPass()"
						></el-input>
						<p class="restriction-text">
							비밀번호는 8-20자 이내로 영문
							대,소문자/숫자/특수문자(.,!,@,#,$,%)를 3가지 이상 조합하여 입력해
							주세요. 연속된 숫자 또는 4자 이상의 동일 문자는 비밀번호로 사용할
							수 없습니다.
						</p>

						<el-button
							class="next-btn block-btn"
							type="primary"
							:disabled="
								resetPass.password !== resetPass.comfirmPassword ||
									resetPass.password.length < 8
							"
							@click="checkPass()"
							>비밀번호 변경</el-button
						>
					</div>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import mixin from 'mixins/mixin'
import UserService from 'service/user-service'
export default {
	mixins: [mixin],
	data() {
		return {
			step: 'number',
			resetPass: {
				email: '',
				password: '',
			},
			uuid: null,
			answer: '',
			question: null,
			userId: null,
			isQuestionAuth: false,
			findUserData: {
				email: null,
				signUpDate: null,
			},
			questionList: [
				'첫 반려동물의 이름은 무엇입니까?',
				'10대 시절에 가장 친하게 지냈던 친구의 이름은 무엇입니까?',
				'처음 배운 요리는 무엇입니까?',
				'영화관에서 처음으로 관람한 영화는 무엇입니까?',
				'처음으로 비행기를 타고 방문한 곳은 어디입니까?',
				'가장 좋아했던 동화책의 제목은 무엇입니까?',
				'부모님이 처음 만난 도시는 어디입니까?',
				'처음으로 가보았던 해변의 이름은 무엇입니까?',
				'처음으로 가보았던 산의 이름은 무엇입니까?',
				'가장 좋아했던 선생님은 누구였습니까?',
				'내가 좋아하는 차종은 무엇입니까?',
			],
		}
	},
	methods: {
		async checkAuth() {
			try {
				const res = await UserService.userAuth({
					email: this.resetPass.email,
				})
				if (res.result) {
					this.nextStep('question')
				} else throw res
			} catch (e) {
				this.alertMessage(
					'ID 불일치',
					'등록된 ID가 없습니다. 마스터에게 계정 생성을 요청하세요.',
					'error',
				)
			}
		},
		async checkAnswer() {
			try {
				const res = await UserService.userCheckAnswer({
					email: this.resetPass.email,
					question: this.question,
					answer: this.answer,
				})
				// console.log(res)
				if (res.uuid) {
					this.uuid = res.uuid
					this.nextStep('resetPass')
				} else throw res
			} catch (e) {
				this.alertMessage(
					'비밀번호 찾기 질문/답변 불일치 오류',
					'입력하신 질문/답변이 일치하지 않습니다. 다시 한 번 확인해 주세요.',
					'error',
				)
			}
		},
		nextStep(step) {
			this.step = step
			if (step == 'resetPass') {
				this.isQuestionAuth = true
			}
		},
		async changePass() {
			try {
				const res = await UserService.putUserPassChange({
					uuid: this.uuid,
					email: this.resetPass.email,
					password: this.resetPass.password,
				})
				if (res.code === 200) {
					this.confirmWindow(
						'비밀번호 변경 완료',
						'기존 로그인된 기기에서 로그아웃 됩니다. 변경된 새 비밀번호로 다시 로그인해 주세요.',
						true,
					)
				} else throw res
			} catch (res) {
				if (res.code === 4009)
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
.find-body {
	padding-top: 44px;
	padding-right: 40px;
	padding-bottom: 52px;
	padding-left: 40px;
	font-size: 16px;
	text-align: left;
	.info-text {
		margin-bottom: 4px;
		color: #4c4c4d;
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

.step-warp {
	margin-top: 40px;
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
	text-align: center;
	background: #f7f7f7;
	i {
		padding-right: 20px;
		color: #80838a;
	}
}
</style>
