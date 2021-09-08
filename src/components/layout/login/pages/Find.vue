<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('find.title') }}</h2>
        <p class="disc" v-html="$t('find.pageInfo')"></p>

        <section class="find-wrap">
          <div class="find-head">
            <button
              :class="{ active: tab == 'email' }"
              @click="tabChange('email')"
            >
              <span>{{ $t('find.findEmail') }}</span>
            </button>
            <button
              :class="{ active: tab == 'reset_password' }"
              @click="tabChange('reset_password')"
            >
              <span>{{ $t('find.resetPassword.title') }}</span>
            </button>
          </div>
          <div class="find-body" v-if="tab == 'email'">
            <p class="info-text" v-html="mailFindText"></p>

            <div v-if="isFindEmail === null">
              <el-radio v-model="tabCategory" label="1">{{
                $t('find.number')
              }}</el-radio>
              <div v-show="tabCategory == '1'">
                <p class="input-title">{{ $t('find.name.title') }}</p>
                <el-input
                  class="lastname-input"
                  :placeholder="$t('find.name.lastName')"
                  clearable
                  name="tab-category-1-lastname"
                  v-validate.immediate="'required'"
                  v-model="fullName.lastName"
                  :class="{
                    'input-danger': errors.has('tab-category-1-lastname'),
                  }"
                ></el-input>
                <el-input
                  class="firstname-input"
                  :placeholder="$t('find.name.firstName')"
                  clearable
                  name="tab-category-1-firstname"
                  v-validate.immediate="'required'"
                  v-model="fullName.firstName"
                  :class="{
                    'input-danger': errors.has('tab-category-1-firstname'),
                  }"
                ></el-input>
                <p class="input-title">{{ $t('find.mobile.title') }}</p>
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
                    :label="item.value"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
                <el-input
                  :placeholder="$t('find.mobile.placeHolder')"
                  class="phonenumber-input"
                  clearable
                  name="phoneNumber"
                  v-validate.immediate="'required'"
                  v-model="findEmail.phoneNumber"
                  :class="{ 'input-danger': errors.has('phoneNumber') }"
                ></el-input>
              </div>

              <el-radio v-model="tabCategory" label="2">{{
                $t('find.recoveryMail')
              }}</el-radio>

              <div v-show="tabCategory == '2'">
                <p class="input-title">{{ $t('find.name.title') }}</p>
                <el-input
                  class="lastname-input"
                  :placeholder="$t('find.name.lastName')"
                  clearable
                  name="tab-category-2-lastname"
                  v-validate.immediate="'required'"
                  v-model="fullName.lastName"
                  :class="{
                    'input-danger': errors.has('tab-category-2-lastname'),
                  }"
                ></el-input>
                <el-input
                  class="firstname-input"
                  :placeholder="$t('find.name.firstName')"
                  clearable
                  name="tab-category-2-firstname"
                  v-validate.immediate="'required'"
                  v-model="fullName.firstName"
                  :class="{
                    'input-danger': errors.has('tab-category-2-firstname'),
                  }"
                ></el-input>
                <p class="input-title">
                  {{ $t('user.recoveryEmail.title') }}
                </p>
                <el-input
                  type="email"
                  :placeholder="$t('user.recoveryEmail.placeHolder')"
                  clearable
                  name="recoveryEmail"
                  v-validate.immediate="'required|email|max:50'"
                  v-model="findEmail.recoveryEmail"
                  :class="{ 'input-danger': errors.has('recoveryEmail') }"
                ></el-input>
              </div>
              <el-button
                class="next-btn block-btn"
                type="primary"
                @click="mailAccountFind"
                :disabled="emailFindActive"
                >{{ $t('find.findBtn') }}</el-button
              >
            </div>
            <div v-else class="mailfind-before">
              <div
                class="user-email-holder"
                v-for="(user, idx) of findUserData"
                :key="idx"
              >
                <p>
                  <i>{{ $t('login.email') }}:</i>
                  <span>{{ user.email }}</span>
                </p>
                <p>
                  <i>{{ $t('find.signupDate') }}:</i>
                  <span>{{ user.signUpDate }}</span>
                </p>
              </div>
              <el-button
                class="next-btn block-btn"
                type="primary"
                @click="$router.push({ name: 'login' })"
                >{{ $t('login.title') }}</el-button
              >
            </div>
          </div>
          <div class="find-body" v-if="tab == 'reset_password'">
            <div v-if="isCodeAuth === null">
              <p class="info-text" v-html="$t('find.feedbackInfo')"></p>

              <p class="input-title">{{ $t('login.email') }}</p>
              <el-input
                :placeholder="$t('login.emailPlaceholder')"
                clearable
                type="email"
                v-model="resetPass.email"
              ></el-input>
              <el-button
                class="next-btn block-btn"
                type="primary"
                @click="emailPassCode"
                :disabled="!emailValid"
                >{{ $t('find.authCode.done.send.title') }}</el-button
              >
            </div>
            <div v-else class="auth-before">
              <p class="info-text">
                {{ authText }}
              </p>
              <div class="user-email-holder">
                <p>
                  <i>{{ $t('login.email') }}:</i>
                  <span>{{ resetPass.email }}</span>
                </p>
              </div>

              <el-input
                :placeholder="$t('find.authInput.placeholder')"
                type="text"
                v-model="resetPass.authCode"
                maxlength="6"
                v-if="isCodeAuth === false"
              ></el-input>

              <p class="input-title must-check" v-if="isCodeAuth === true">
                {{ $t('login.password') }}
              </p>
              <el-input
                :placeholder="$t('find.authInput.newPass')"
                type="password"
                name="password"
                show-password
                v-model="resetPass.password"
                v-if="isCodeAuth === true"
                v-validate="'required|password'"
                :class="{ 'input-danger': errors.has('password') }"
              ></el-input>
              <el-input
                :placeholder="$t('find.authInput.newPassConfirm')"
                type="password"
                v-model="resetPass.comfirmPassword"
                v-if="isCodeAuth === true"
                show-password
                name="passwordConfirm"
                v-validate="'required|password'"
                :class="{
                  'input-danger':
                    resetPass.password !== resetPass.comfirmPassword ||
                    errors.has('password'),
                }"
              ></el-input>
              <p
                class="restriction-text"
                v-if="isCodeAuth === true"
                v-html="$t('find.authInput.rule')"
              ></p>

              <el-button
                class="next-btn block-btn"
                type="primary"
                :disabled="resetPass.authCode.length < 6"
                @click="authCodeCheck"
                v-if="isCodeAuth === false"
                >{{ $t('find.authInput.code') }}</el-button
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
                >{{ $t('find.authInput.change') }}</el-button
              >
            </div>
          </div>
        </section>
        <el-button class="inquiry-btn" v-if="false"
          ><i class="el-icon-warning-outline"></i
          >{{ $t('find.inquiry') }}</el-button
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
        countryCode: '+82',
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
      findUserData: [],
      countryCodeLists: CountryCode.countryCode,
    }
  },
  mounted() {
    if (this.$props.findCategory === 'email') return (this.tab = 'email')
    else return (this.tab = 'reset_password')
  },
  watch: {
    tabCategory() {
      this.findEmail.phoneNumber = ''
      this.findEmail.recoveryEmail = ''
    },
  },
  computed: {
    emailFindActive() {
      if (this.tabCategory == 1) {
        if (
          this.$validator.errors.has('tab-category-1-firstname') ||
          this.$validator.errors.has('tab-category-1-lastName') ||
          this.$validator.errors.has('phoneNumber')
        )
          return true
        else return false
      } else {
        if (
          this.$validator.errors.has('tab-category-2-firstname') ||
          this.$validator.errors.has('tab-category-2-lastName') ||
          this.$validator.errors.has('recoveryEmail')
        )
          return true
        else return false
      }
    },
    emailValid() {
      const checkEmail = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/
      if (!checkEmail.test(this.resetPass.email)) return false
      else return true
    },
    mailFindText() {
      if (this.isFindEmail) return this.$t('find.EmailFindText.after')
      // 입력하신 정보와 일치하는 회원의 정보입니다. 정보 보호를 위해 앞 4자리만 보여집니다.
      else return this.$t('find.EmailFindText.before')
      //회원 정보에 등록된 사용자 정보로 이메일을 찾습니다. 원하는 방법을 선택해 주세요.
    },
    authText() {
      if (this.isCodeAuth) return this.$t('find.authText.after')
      // 새 비밀번호를 설정합니다. 비밀번호 변경 시, 기존 로그인된 디바이스에서 로그인이 해지됩니다.
      else return this.$t('find.authText.before')
      // 보안 코드 6자리를 입력해 주세요.
    },
    mobileSet() {
      if (
        this.findEmail.countryCode === '' ||
        this.findEmail.phoneNumber === ''
      )
        return ''
      else
        return `${
          this.findEmail.countryCode
        }-${this.findEmail.phoneNumber.replace(/[^0-9+]/g, '')}`
    },
  },
  methods: {
    tabChange(tab) {
      this.tab = tab
      this.$router.push({ name: 'findTab', params: { findCategory: tab } })
    },
    async mailAccountFind() {
      try {
        const res = await UserService.userFindEmail({
          params: {
            firstName: this.fullName.firstName,
            lastName: this.fullName.lastName,
            mobile: this.mobileSet,
            recoveryEmail: this.findEmail.recoveryEmail,
          },
        })
        if (res.code === 200) {
          this.isFindEmail = true
          this.findUserData = res.data.emailFindInfoList
          // console.log(this.findUserData)
        } else throw res
      } catch (e) {
        // console.log(e)
        if (e.code === 4002)
          return this.alertMessage(
            this.$t('find.accountError.notSync.title'), // 계정정보 불일치
            this.$t('find.accountError.notSync.message'), // 입력하신 정보와 일치하는 VIRNECT 회원이 없습니다.
            'error',
          )
        else
          return this.alertMessage(
            this.$t('find.accountError.etc.title'), // 계정정보 전송 오류
            this.$t('find.accountError.etc.message'), // 계정정보 전송에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    },
    async changePass() {
      const params = {
        uuid: this.userId,
        email: this.userEmail,
        password: this.resetPass.password,
      }
      try {
        const res = await UserService.userPassChange({ params: params })
        // console.log(res)
        if (res.code === 200)
          return this.confirmWindow(
            this.$t('find.resetPassword.done.title'), // 비밀번호 변경 완료
            this.$t('find.resetPassword.done.message'), // 기존 로그인된 기기에서 로그아웃 됩니다. 변경된 새 비밀번호로 다시 로그인해 주세요.
            this.$t('login.accountError.btn'), // 확인
          )
        else throw res
      } catch (e) {
        if (e.code === 4009)
          return this.alertMessage(
            this.$t('find.resetPassword.error.title'), // 비밀번호 재설정 실패
            this.$t('find.resetPassword.error.multiple'), // 이전과 동일한 비밀번호는 새 비밀번호로 설정할 수 없습니다.
            'error',
          )
        else
          return this.alertMessage(
            this.$t('find.resetPassword.error.title'), // 비밀번호 재설정 실패
            this.$t('find.resetPassword.error.etc'), // 비밀번호 변경에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    },
    async emailPassCode() {
      // 비밀번호 재설정 이메일 확인
      try {
        let res = await UserService.userPass({
          params: { email: this.resetPass.email },
        })
        if (res.code === 200) {
          this.alertMessage(
            this.$t('find.authCode.done.send.title'), // 보안코드 전송
            this.$t('find.authCode.done.send.message'), // 가입하신 이메일로 보안 코드가 전송되었습니다. 이메일의 보안 코드를 확인하여 입력해 주세요.
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
            this.$t('find.authCode.error.send'), // 보안코드 전송 오류
            this.$t('find.authCode.error.nobody'), // 입력하신 정보와 일치하는 VIRNECT 회원이 없습니다.
            'error',
          )
        else if (e.code === 9999)
          return this.alertMessage(
            this.$t('find.authCode.error.send'), // 보안코드 전송 오류
            this.$t('find.authCode.error.timeOut'),
            // 보안 코드는 보안 유지를 위해 발급 후 1분 내에 재발송 되지 않습니다. 계정의 메일함을 확인해 주시거나, 잠시 후 다시 이용해 주세요
            'error',
          )
        else
          return this.alertMessage(
            this.$t('find.authCode.error.send'), // 보안코드 전송 오류
            this.$t('find.authCode.error.etc'), // 보안코드 이메일 전송에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    },
    async authCodeCheck() {
      try {
        let res = await UserService.userCodeCheck({
          params: {
            code: this.resetPass.authCode,
            email: this.resetPass.email,
          },
        })
        if (res.code === 200) {
          this.userId = res.data.uuid
          this.userEmail = res.data.email
          this.alertMessage(
            this.$t('find.authCode.done.sync.title'), // 보안코드 일치
            this.$t('find.authCode.done.sync.message'), // 보안 코드 인증이 완료되었습니다.
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
            this.$t('find.authCode.error.notSyncCode'), // 보안코드 불일치 오류
            this.$t('find.authCode.error.sendMessage'), // 입력하신 보안 코드가 일치하지 않습니다. 다시 한 번 확인해 주세요.
            'error',
          )
        else
          return this.alertMessage(
            this.$t('find.authCode.error.auth'), // 보안코드 인증 오류
            this.$t('find.authCode.error.etc'), // 보안 코드 인증에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    },
    checkPass() {
      if (this.passValidate(this.resetPass.password)) {
        this.changePass()
      } else {
        this.alertMessage(
          this.$t('find.authCode.error.rulePass'), // 비밀번호 입력 오류
          this.$t('find.authCode.error.rulePassMessage'),
          // 비밀번호는 8~20자 이내로 영문 대,소문자/숫자/특수문자( . , !, @, #, $, % )를 3가지 이상 조합하여 입력해 주세요. 연속된 숫자 또는 4자 이상의 동일 문자는 비밀번호로 사용할 수 없습니다.
          'error',
        )
      }
    },
  },
  created() {
    this.$validator.extend('password', {
      getMessage: () => this.$t('signup.password.notice'),
      validate: value => this.passValidate(value),
    })
  },
}
</script>

<style lang="scss" scoped>
.row-bg > div {
  width: 460px;
  font-weight: 500;
  @media (max-width: $mobile) {
    padding-right: 0;
    padding-left: 0;
  }
}
.find-wrap {
  margin-top: 52px;
  border: 1px solid #eaedf3;
  border-radius: 4px;

  @media (max-width: $mobile) {
    margin-top: 32px;
    border: none;
  }
}
.find-head {
  font-size: 0;
  border-bottom: 1px solid #eaedf3;
  button {
    width: 50%;
    height: 60px;
    color: #0d2a58;
    font-size: 16px;
    border: 0;
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
  margin-right: 0;
  white-space: inherit;
}
.find-body {
  padding-top: 44px;
  padding-right: 40px;
  padding-bottom: 52px;
  padding-left: 40px;
  font-size: 16px;
  text-align: left;
  @media (max-width: $mobile) {
    padding-top: 28px;
    padding-right: 24px;
    padding-left: 24px;
    font-size: 15px;
  }

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
