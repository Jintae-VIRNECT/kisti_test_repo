<template>
	<div class="container">
		<el-row type="flex" justify="center" align="middle" class="row-bg">
			<el-col>
				<h2>추가 정보 입력</h2>
				<p>
					추가 정보를 입력하시면 VIRNECT 제품을 <br />더 유용하게 사용하실 수
					있습니다.
				</p>

				<p class="input-title">프로필 이미지</p>
				<article class="profile-image">
					<div class="image-holder" @click="profilePopup = true">
						<div
							class="image"
							v-if="thumbnail"
							:style="`background-image: url(${thumbnail})`"
						></div>
						<i class="camera-ico"
							><img src="~assets/images/common/ic-camera-alt@2x.png"
						/></i>
					</div>
					<div class="text-wrap">
						<p>프로필 이미지를 등록해 주세요.</p>
						<p>등록하신 프로필 이미지는 다른 사용자들에게 보여집니다.</p>
					</div>
				</article>
				<el-dialog
					title="프로필 이미지 설정"
					:visible.sync="profilePopup"
					width="30%"
					:before-close="handleClose"
				>
					<div>
						<p class="contents">
							프로필 이미지가 전체 VIRNECT 사용자에게 보여집니다.
						</p>
						<div class="image-holder pop-profile" @click="profilePopup = true">
							<input
								type="file"
								id="profileImage"
								@change="uploadImage"
								ref="imgUpload"
							/>
							<label for="profileImage" class="avatar">
								<div
									class="image"
									v-if="thumbnail"
									:style="`background-image: url(${thumbnail})`"
								></div>
							</label>
						</div>
						<div class="el-upload__tip">
							최대 5Mb의 Jpg, Png 파일 업로드 가능
						</div>
					</div>
					<span slot="footer" class="dialog-footer">
						<el-button type="info" @click.native="uploadBtn" class="left-btn"
							>이미지 업로드</el-button
						>
						<el-button @click="deleteImage">삭제</el-button>
						<el-button type="primary" @click="profileDone" :disabled="disabled"
							>이미지 등록</el-button
						>
					</span>
				</el-dialog>

				<p class="input-title">닉네임</p>
				<el-input
					:placeholder="nicknameSet"
					v-model="user.nickname"
					type="text"
					name="nickname"
					v-validate="'min:2|max:20'"
					:class="{ 'input-danger': errors.has('nickname') }"
					clearable
				>
				</el-input>
				<p class="restriction-text">
					국문, 영문, 특수문자(&lt;),(&gt;) 제외, 띄어쓰기 포함 20자 이하로
					입력해 주세요.
				</p>

				<dl class="recover-info">
					<dt>계정 복구 정보 입력</dt>
					<dd>계정 분실 시 본인 확인을 위한 정보를 입력해 주세요.</dd>
				</dl>

				<p class="input-title">연락처</p>
				<el-select
					v-model="user.countryCode"
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
					class="phonenumber-input"
					placeholder="전화번호를 입력해 주세요"
					v-model="user.mobile"
					clearable
					type="text"
					name="mobile"
				></el-input>

				<p class="input-title">복구 이메일 주소</p>
				<el-input
					placeholder="복구 이메일 주소를 입력해 주세요"
					v-model="user.recoveryEmail"
					type="email"
					name="recoveryEmail"
					clearable
					v-validate="'email|max:50'"
					:class="{ 'input-danger': errors.has('recoveryEmail') }"
				>
				</el-input>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="checkNickName()"
					:disabled="
						(user.nickname == '' || user.nickname == null) &&
							user.mobile == null &&
							user.recoveryEmail == null
					"
					>확인</el-button
				>
				<el-button class="block-btn" @click="later">나중에 하기</el-button>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import Cookies from 'js-cookie'
import CountryCode from 'model/countryCode'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'

export default {
	name: 'user',
	mixins: [mixin],
	props: {
		signup: Object,
	},
	data() {
		return {
			profilePopup: false,
			user: {
				profile: null,
				nickname: null,
				countryCode: null,
				mobile: null,
				recoveryEmail: null,
			},
			countryCodeLists: CountryCode.countryCode,
			submitted: false,
			successful: false,
			isSendEmail: false,
			isValidEmail: false,
			isShow: false,
			message: null,
			formData: new FormData(),
			file: null,
			thumbnail: null,
		}
	},
	watch: {
		signup() {
			console.log(this.signup)
		},
	},
	computed: {
		disabled() {
			return this.file === this.user.profile
		},
		nicknameSet() {
			return `${this.$props.signup.lastName}${this.$props.signup.firstName}`
		},
		mobileSet() {
			if (this.user.countryCode === null || this.user.mobile === null) return ''
			else return `${this.user.countryCode}-${this.user.mobile}`
		},
	},
	mounted() {
		if (this.loggedIn || !this.$props.signup) {
			this.$router.push('/')
		}
	},
	methods: {
		async handleRegisterDetail() {
			this.formData = new FormData()
			// 상세정보등록

			this.formData.append('email', this.$props.signup.email)
			this.formData.append('password', this.$props.signup.password)
			this.formData.append('firstName', this.$props.signup.firstName)
			this.formData.append('lastName', this.$props.signup.lastName)
			this.formData.append('birth', this.$props.signup.birth)
			this.formData.append(
				'marketInfoReceive',
				this.$props.signup.marketInfoReceive,
			)
			this.formData.append('joinInfo', this.$props.signup.joinInfo)
			this.formData.append('serviceInfo', this.$props.signup.serviceInfo)
			this.formData.append('sessionCode', this.$props.signup.sessionCode)

			this.formData.append('profile', this.user.profile)
			this.formData.append('nickname', this.user.nickname)
			this.formData.append('mobile', this.mobileSet)
			this.formData.append('recoveryEmail', this.user.recoveryEmail)

			let registerData = null

			// 회원가입
			try {
				registerData = await AuthService.signUp(this.formData)
				if (registerData.code === 200) {
					// console.log(registerData)
					setTimeout(() => {
						window.scrollTo({
							left: 0,
							top: 0,
						})
					}, 400)
					this.$router.push({
						name: 'complete',
					})
				} else throw registerData
			} catch (error) {
				this.alertMessage(
					'기타 오류',
					`회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.`,
					'error',
				)
			}
		},
		async later() {
			this.formData = new FormData()
			try {
				this.formData.append('email', this.$props.signup.email)
				this.formData.append('password', this.$props.signup.password)
				this.formData.append('firstName', this.$props.signup.firstName)
				this.formData.append('lastName', this.$props.signup.lastName)
				this.formData.append('birth', this.$props.signup.birth)
				this.formData.append(
					'marketInfoReceive',
					this.$props.signup.marketInfoReceive,
				)
				this.formData.append('joinInfo', this.$props.signup.joinInfo)
				this.formData.append('serviceInfo', this.$props.signup.serviceInfo)
				this.formData.append('sessionCode', this.$props.signup.sessionCode)

				let res = await AuthService.signUp(this.formData)
				// console.log(res)
				if (res.code === 200) {
					this.$router.push({
						name: 'complete',
					})
					const cookieOption = {
						expires: res.data.expireIn / 3600000,
						domain:
							location.hostname.split('.').length === 3
								? location.hostname.replace(/.*?\./, '')
								: location.hostname,
					}
					Cookies.set('accessToken', res.data.accessToken, cookieOption)
					Cookies.set('refreshToken', res.data.refreshToken, cookieOption)
					// localStorage.setItem('user', JSON.stringify(res.data))
				} else throw res
			} catch (e) {
				this.alertMessage(
					'기타 오류',
					`회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.`,
					'error',
				)
			}
		},
		handleClose(done) {
			done()
		},
		uploadImage(event) {
			// console.log(event)
			const files = event.target.files
			// console.log(files[files.length - 1])
			this.formData.delete('profile') // profile는 컨텐츠 내의 이미지 리소스
			this.validImage(event)
				.then(imageData => {
					// console.log(files[files.length - 1])
					this.file = files[files.length - 1]
					this.thumbnail = imageData
				})
				.catch(error => {
					console.log(error)
				})
		},
		uploadBtn() {
			this.$refs.imgUpload.click()
		},
		profileDone() {
			this.profilePopup = false
			this.user.profile = this.file
		},
		deleteImage() {
			// this.$refs.upload.clearFiles()
			this.user.profile = null
			this.file = null
			this.thumbnail = null
		},
		validImage(event) {
			const files = event.target.files
			return new Promise((resolve, reject) => {
				if (files.length > 0) {
					if (
						['image/gif', 'image/jpeg', 'image/jpg', 'image/png'].indexOf(
							files[0].type,
						) < 0
					) {
						reject('This image is unavailable.')
						return
					}
					if (files[0].size > 2 * 1024 * 1024) {
						reject('This image size is unavailable.')
						return
					}
					this.file = null

					const oReader = new FileReader()
					oReader.onload = e => {
						const imageData = e.target.result
						const oImg = new Image()
						oImg.onload = _event => {
							resolve(imageData)
							_event.target.remove()
						}
						oImg.onerror = _event => {
							//이미지 아닐 시 처리.
							reject('This image is unavailable.')
						}
						oImg.src = imageData
					}
					oReader.readAsDataURL(files[0])
				}
			})
		},
		async checkNickName() {
			try {
				let res = this.nickNameValidate(this.user.nickname)
				if (res === true) {
					this.handleRegisterDetail()
				} else throw res
			} catch (e) {
				this.alertMessage(
					'닉네임 설정 오류',
					`닉네임은 국문, 영문, 특수문자, 띄어쓰기 포함 20자 이하로 입력해 주세요.`,
					'error',
				)
			}
		},
		nickNameValidate(nickName) {
			if (/\s/.test(nickName)) return false
			if (/[`~!@#$%^&*|\\\'\";:\/?]/gi.test(nickName)) return false
			return true
		},
	},
}
</script>

<style lang="scss" scoped>
.el-dialog {
	border: solid 1px #e6e9ee;
	border-radius: 4px;
	box-shadow: 0 2px 8px 0 rgba(8, 23, 48, 0.06),
		0 2px 4px 0 rgba(8, 23, 48, 0.1);

	.image-holder {
		float: none;
		width: 160px;
		height: 160px;
		margin: 44px auto 16px;
		img {
			width: 100%;
			height: 100%;
			-webkit-mask: url('~assets/images/common/ic-bg.svg') no-repeat;
			-webkit-mask-size: 100%;
			mask: url('~assets/images/common/ic-bg.svg') no-repeat;
			mask-size: 100%;
		}
	}
	&__wrapper {
		text-align: left;
	}
	input[type='file'] {
		position: absolute;
		top: 0;
		left: -9999px;
	}
	label {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		cursor: pointer;
	}
	.el-button {
		height: 36px;
		font-weight: normal;
		font-size: 13px;
		&.left-btn {
			float: left;
		}
	}
}
.el-button.next-btn {
	margin-top: 60px;
}
.el-upload__tip {
	color: #566173;
	font-size: 13px;
	text-align: center;
}

.image-holder {
	position: relative;
	float: left;
	width: 80px;
	height: 80px;
	margin: 20px 16px 0;
	text-align: center;
	background: url('~assets/images/common/ic-bg.svg') no-repeat;
	background-size: 100%;
	cursor: pointer;
	&:before {
		position: absolute;
		top: 0;
		left: 0;
		display: block;
		width: 80%;
		height: 80%;
		margin: 10%;
		background: url('~assets/images/common/ic-user-profile.svg') no-repeat;
		background-size: 100%;
		content: '';
	}
	.image {
		width: 100%;
		height: 100%;
		background-position: center;
		background-size: cover;
		-webkit-mask: url('~assets/images/common/ic-bg.svg') no-repeat;
		-webkit-mask-size: 100%;
		mask: url('~assets/images/common/ic-bg.svg') no-repeat;
		mask-size: 100%;
	}
	// .avatar {
	// 	max-width: 80%;
	// }
}
</style>
