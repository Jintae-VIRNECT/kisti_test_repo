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
							회원 정보에 등록된 사용자 정보로 이메일을 찾습니다. 원하는 방법을
							선택해 주세요.
						</p>

						<el-radio v-model="tabCategory" label="1"
							>회원 정보에 등록된 휴대폰 번호로 찾기</el-radio
						>
						<template v-if="tabCategory == '1'">
							<p class="input-title">이름</p>
							<el-input
								class="firstname-input"
								placeholder="성"
								clearable
								name="firstname"
								v-validate="'required'"
								v-model="fullName.firstName"
							></el-input>
							<el-input
								class="lastname-input"
								placeholder="이름"
								clearable
								name="lastname"
								v-validate="'required'"
								v-model="fullName.lastName"
							></el-input>
							<p class="input-title">휴대폰 번호</p>
							<el-input
								placeholder="휴대폰 번호를 입력해 주세요"
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
								class="firstname-input"
								placeholder="성"
								clearable
								name="firstname"
								v-validate="'required'"
								v-model="fullName.firstName"
							></el-input>
							<el-input
								class="lastname-input"
								placeholder="이름"
								clearable
								name="lastname"
								v-validate="'required'"
								v-model="fullName.lastName"
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
							@click="
								$router.push({
									name: 'register',
									params: { marketInfoReceive: marketingAgree },
								})
							"
							:disabled="emailFindActive"
							>이메일 찾기</el-button
						>
					</div>
					<div class="find-body" v-if="tab == 'resetPassword'">
						<p class="info-text">
							회원 가입 시 입력한 등록된 이메일 주소로 비밀번호 재설정 보안
							코드를 발송해 드립니다.
						</p>

						<p class="input-title">이메일</p>
						<el-input
							placeholder="이메일 주소를 입력해 주세요"
							clearable
							name="email"
							v-validate="'required|email|max:50'"
							v-model="resetPass.email"
						></el-input>
						<el-button
							class="next-btn block-btn"
							type="primary"
							@click="
								$router.push({
									name: 'register',
									params: { marketInfoReceive: marketingAgree },
								})
							"
							:disabled="resetPass.email.length <= 8"
							>보안코드 전송</el-button
						>
					</div>
				</section>
				<el-button class="inquiry-btn"
					><i class="el-icon-warning-outline"></i>문의하기</el-button
				>
			</el-col>
		</el-row>
	</div>
</template>

<script>
export default {
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
				phoneNumber: '',
				recoveryEmail: '',
			},
			resetPass: {
				email: '',
			},
		}
	},
	mounted() {
		if (this.findCategory === 'email') return (this.tab = 'email')
		else return (this.tab = 'resetPassword')
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
</style>
