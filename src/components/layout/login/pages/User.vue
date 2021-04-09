<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('user.title') }}</h2>
        <p class="disc" v-html="$t('user.pageInfo')"></p>

        <p class="input-title">{{ $t('user.profileImage.title') }}</p>
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
            <p>{{ $t('user.profileImage.subTitle') }}</p>
            <p>{{ $t('user.profileImage.contents') }}</p>
          </div>
        </article>
        <el-dialog
          :title="$t('user.profileImage.imageSet')"
          :visible.sync="profilePopup"
          width="400px"
          :before-close="handleClose"
          @open="handleOpen"
        >
          <div>
            <p class="contents">
              {{ $t('user.profileImage.visible') }}
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
                  v-if="inputImg"
                  :style="`background-image: url(${inputImg})`"
                ></div>
              </label>
            </div>
            <div class="el-upload__tip">
              {{ $t('user.profileImage.limit') }}
            </div>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button
              type="info"
              @click.native="uploadBtn"
              class="left-btn"
              :disabled="inputImg !== null"
              >{{ $t('user.profileImage.upload') }}</el-button
            >
            <el-button @click="deleteImage">{{
              $t('user.profileImage.delete')
            }}</el-button>
            <el-button
              type="primary"
              @click="profileDone"
              :disabled="disabled"
              >{{ $t('user.profileImage.submit') }}</el-button
            >
          </span>
        </el-dialog>

        <p class="input-title">{{ $t('user.nickName.title') }}</p>

        <el-input
          v-model="user.nickname"
          :placeholder="nicknameSet"
          type="text"
          name="nickname"
          v-validate="'min:2|max:20'"
          :class="{ 'input-danger': errors.has('nickname') }"
          clearable
        >
        </el-input>
        <p class="restriction-text" v-html="$t('user.nickName.contents')"></p>

        <dl class="recover-info">
          <dt>{{ $t('user.recoveryInfo.title') }}</dt>
          <dd>{{ $t('user.recoveryInfo.placeHolder') }}</dd>
        </dl>

        <p class="input-title">{{ $t('user.phoneNumber.title') }}</p>
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
            :label="item.value"
            :value="item.value"
          >
          </el-option>
        </el-select>

        <el-input
          class="phonenumber-input"
          :placeholder="$t('user.phoneNumber.contents')"
          v-model="user.mobile"
          clearable
          type="text"
          name="mobile"
        ></el-input>

        <p class="input-title">{{ $t('user.recoveryEmail.title') }}</p>
        <el-input
          :placeholder="$t('user.recoveryEmail.placeHolder')"
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
          :disabled="!checkConfirm"
          >{{ $t('user.confirm') }}</el-button
        >
        <el-button class="block-btn" @click="later">{{
          $t('user.later')
        }}</el-button>
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
    // signup: {
    // 	default() {
    // 		return {}
    // 	},
    // },
  },
  data() {
    return {
      profilePopup: false,
      user: {
        profile: '',
        nickname: '',
        countryCode: '+82',
        mobile: '',
        recoveryEmail: '',
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
      inputImg: null,
    }
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
      else
        return `${this.user.countryCode}-${this.user.mobile.replace(
          /[^0-9+]/g,
          '',
        )}`
    },
    checkConfirm() {
      if (
        this.user.nickname !== '' ||
        this.user.mobile !== '' ||
        this.user.recoveryEmail !== '' ||
        this.user.profile !== ''
      )
        return true
      return false
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
      this.formData.append('inviteSession', this.$props.signup.inviteSession)
      if (this.user.profile !== '') {
        this.formData.append('profile', this.user.profile)
      }
      if (this.user.nickName !== '') {
        this.formData.append('nickname', this.user.nickname)
      } else {
        this.formData.append('nickname', this.nicknameSet)
      }
      this.formData.append('mobile', this.mobileSet)
      if (this.user.recoveryEmail !== '') {
        this.formData.append('recoveryEmail', this.user.recoveryEmail)
      }

      let registerData = null

      // 회원가입
      try {
        registerData = await AuthService.signUp({ params: this.formData })
        if (registerData.code === 200) {
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
          this.$t('user.error.etc.title'), // 기타 오류
          this.$t('user.error.etc.message'), // 회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.
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
        this.formData.append('inviteSession', this.$props.signup.inviteSession)

        let res = await AuthService.signUp({ params: this.formData })
        // console.log(res)
        if (res.code === 200) {
          this.$router.push({
            name: 'complete',
          })
          const cookieOption = {
            secure: true,
            sameSite: 'None',
            expires: res.data.expireIn / 3600000,
            domain:
              location.hostname.split('.').length === 3
                ? location.hostname.replace(/.*?\./, '')
                : location.hostname,
          }
          Cookies.set('accessToken', res.data.accessToken, cookieOption)
          Cookies.set('refreshToken', res.data.refreshToken, cookieOption)
        } else throw res
      } catch (e) {
        this.alertMessage(
          this.$t('user.error.etc.title'), // 기타 오류
          this.$t('user.error.etc.message'), // 회원가입 진행에 실패하였습니다. 잠시 후 다시 이용해 주세요.
          'error',
        )
      }
    },
    handleOpen() {
      this.inputImg = this.thumbnail
    },
    handleClose(done) {
      this.file = null
      this.$refs.imgUpload.value = ''
      done()
    },
    uploadImage(event) {
      const files = event.target.files
      this.formData.delete('profile') // profile는 컨텐츠 내의 이미지 리소스
      this.validImage(event)
        .then(imageData => {
          this.file = files[files.length - 1]
          this.inputImg = imageData
        })
        .catch(error => {
          console.log(error)
          this.alertMessage(this.$t('user.error.etc.title'), error, 'error')
        })
    },
    uploadBtn() {
      this.$refs.imgUpload.click()
    },
    profileDone() {
      this.profilePopup = false
      this.user.profile = this.file
      this.thumbnail = this.inputImg
    },
    deleteImage() {
      this.$refs.imgUpload.value = ''
      this.file = null
      this.inputImg = null
    },
    validImage(event) {
      let files = event.target.files
      return new Promise((resolve, reject) => {
        if (files.length > 0) {
          if (
            ['image/jpeg', 'image/jpg', 'image/png'].indexOf(files[0].type) < 0
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
          this.$t('user.error.nickName.title'), // 닉네임 설정 오류
          this.$t('user.error.nickName.message'), // 닉네임은 국문, 영문, 특수문자, 띄어쓰기 포함 20자 이하로 입력해 주세요.
          'error',
        )
      }
    },
    nickNameValidate(nickName) {
      if (/\s/.test(nickName)) return false
      if (/[`~!@#$%^&*|\\\'\";:\/?]/gi.test(nickName)) return false
      if (nickName.length > 20) return false
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

    @media (max-width: $mobile) {
      width: 30vw;
      height: 30vw;
      margin: 16px auto;
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
      @media (max-width: $mobile) {
        display: block;
        float: none;
        margin: 0 auto 24px;
      }
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
