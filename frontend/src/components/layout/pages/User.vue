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
						<!-- <img v-if="file" :src="file" /> -->
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
							<input type="file" id="profileImage" @change="uploadImage" />
							<label for="profileImage" class="avatar">
								<!-- <img v-if="file" :src="file" /> -->
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
						<el-button type="info" @click="uploadBtn" class="left-btn"
							>이미지 업로드</el-button
						>
						<el-button
							@click="
								file = null
								user.profile = null
							"
							>삭제</el-button
						>
						<el-button type="primary" @click="profileDone" :disabled="disabled"
							>이미지 등록</el-button
						>
					</span>
				</el-dialog>

				<p class="input-title">닉네임</p>
				<el-input
					placeholder="장선영"
					v-model="user.nickname"
					type="text"
					name="nickname"
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
				>
				</el-input>

				<el-button
					class="next-btn block-btn"
					type="info"
					@click="handleRegisterDetail()"
					>확인</el-button
				>
				<el-button class="block-btn" @click="later()">나중에 하기</el-button>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import User from 'model/user'
import mixin from 'mixins/mixin'

export default {
	name: 'user',
	mixins: [mixin],
	computed: {
		loggedIn() {
			return this.$store.state.auth.initial.status.loggedIn
		},
	},
	props: {
		signup: Object,
	},
	data() {
		return {
			profilePopup: false,
			user: {
				profile: null,
				nickname: '',
				mobile: '',
				recoveryEmail: '',
				uuid: '',
			},
			countryCodeLists: [
				{
					value: 1,
					label: '+82',
				},
				{
					value: 2,
					label: '+1',
				},
				{
					value: 3,
					label: '+81',
				},
			],
			submitted: false,
			successful: false,
			isSendEmail: false,
			isValidEmail: false,
			isShow: false,
			message: '',
			formData: new FormData(),
			file: null,
			thumbnail: null,
		}
	},
	computed: {
		disabled() {
			return this.file === this.user.profile
		},
	},
	mounted() {
		if (this.loggedIn || !this.$props.signup) {
			this.$router.push('/')
		}
	},
	methods: {
		async handleRegisterDetail() {
			new User(
				this.user.profile,
				this.user.nickname,
				this.user.mobile,
				this.user.recoveryEmail,
				this.user.uuid,
			)
			if (this.user) {
				let registerData = null

				// 회원가입
				try {
					registerData = await this.$store.dispatch(
						'auth/register',
						this.$props.signup,
					)
				} catch (error) {
					if (error) {
						this.alertMessage(
							'기타 오류',
							`회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.`,
							'error',
						)
					}
					return false
				}
				if (!registerData.uuid) return false

				// 상세정보등록
				this.user.uuid = registerData.uuid
				// this.user.uuid = '4d2fce6e509452d6a1e675a50e16e8f0'
				this.formData.append('profile', this.user.profile)
				this.formData.append('nickname', this.user.nickname)
				this.formData.append('mobile', this.user.mobile)
				this.formData.append('recoveryEmail', this.user.recoveryEmail)
				this.formData.append('uuid', this.user.uuid)
				try {
					const detailData = await this.$store.dispatch(
						'auth/userDetail',
						this.formData,
					)
					if (detailData) {
						this.$router.push({
							name: 'complete',
						})
					}
				} catch (error) {
					if (error) {
						this.alertMessage(
							'기타 오류',
							`회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.`,
							'error',
						)
					}
				}
			}
		},
		later() {
			// if (this.$props.signup) {
			this.$store.dispatch('auth/register', this.$props.signup).then(
				data => {
					if (data) {
						// console.log(data.uuid)
						this.$router.push({
							name: 'complete',
						})
						// this.$router.push({
						// 	name: 'user',
						// 	params: { signup: this.signup, uuid: data.uuid },
						// })
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
			// }
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
					console.log(files[files.length - 1])
					this.file = files[files.length - 1]
					this.thumbnail = imageData
				})
				.catch(error => {
					console.log(error)
				})
		},
		uploadBtn() {
			console.log('asdf')
			this.$el.querySelector('#profileImage').dispatchEvent(new Event('click'))
		},
		profileDone() {
			this.profilePopup = false
			this.user.profile = this.file
		},
		deleteImage() {
			this.$refs.upload.clearFiles()
			this.user.profile = null
			this.file = null
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
