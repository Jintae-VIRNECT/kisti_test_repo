<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('onpremise.resetPass.title') }}</h2>
        <p class="disc" v-html="$t('onpremise.resetPass.disc')"></p>

        <div class="find-body" v-if="!isQuestionAuth">
          <p class="input-title must-check">ID</p>
          <el-input
            :placeholder="$t('onpremise.resetPass.input.placeholder')"
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
              >{{ $t('onpremise.resetPass.next') }}</el-button
            >
          </div>
          <div class="step-warp" v-else>
            <p class="input-title must-check">
              {{ $t('onpremise.resetPass.qna.title') }}
            </p>
            <el-select
              v-model="question"
              :placeholder="$t('onpremise.resetPass.qna.placeholder')"
              name="question"
            >
              <el-option
                v-for="item in $t('onpremise.resetPass.questionList')"
                :key="item"
                :label="item"
                :value="item"
              >
              </el-option>
            </el-select>
            <el-input
              :placeholder="$t('onpremise.resetPass.qna.inputPlaceholder')"
              v-if="question !== null"
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
              >{{ $t('onpremise.resetPass.next') }}</el-button
            >
          </div>
        </div>
        <div class="find-wrap" v-else>
          <div class="find-body">
            <p class="info-text">
              {{ $t('onpremise.resetPass.change.notice') }}
            </p>
            <div class="user-email-holder">
              <p>
                <i>ID:</i>
                <span>{{ resetPass.email }}</span>
              </p>
            </div>

            <p class="input-title must-check">
              {{ $t('onpremise.resetPass.change.password') }}
            </p>
            <el-input
              :placeholder="$t('onpremise.resetPass.change.placeholder')"
              type="password"
              name="password"
              show-password
              v-model="resetPass.password"
              v-validate="'required|password'"
              :class="{ 'input-danger': errors.has('password') }"
            ></el-input>
            <el-input
              :placeholder="$t('onpremise.resetPass.change.placeholderRe')"
              type="password"
              v-model="resetPass.comfirmPassword"
              show-password
              name="passwordConfirm"
              v-validate="'required|password'"
              :class="{
                'input-danger':
                  resetPass.password !== resetPass.passwordConfirm ||
                  errors.has('password'),
              }"
              @keyup.enter.native="checkPass()"
            ></el-input>
            <p class="restriction-text">
              {{ $t('onpremise.resetPass.change.passRule') }}
            </p>

            <el-button
              class="next-btn block-btn"
              type="primary"
              :disabled="
                resetPass.password !== resetPass.comfirmPassword ||
                resetPass.password.length < 8
              "
              @click="checkPass()"
              >{{ $t('onpremise.resetPass.change.done') }}</el-button
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
    }
  },
  methods: {
    async checkAuth() {
      try {
        const res = await UserService.userAuth({
          email: this.resetPass.email,
        })
        if (res.code === 200) {
          if (res.data.result) {
            this.nextStep('question')
          } else throw res
        } else throw res
      } catch (e) {
        if (e.code === 4011) {
          this.alertMessage(
            this.$t('onpremise.resetPass.notSet.title'),
            this.$t('onpremise.resetPass.notSet.disc'),
            'error',
          )
        } else {
          this.alertMessage(
            this.$t('onpremise.resetPass.IdDiscord.title'),
            this.$t('onpremise.resetPass.IdDiscord.disc'),
            'error',
          )
        }
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
          this.$t('onpremise.resetPass.answerDiscord.title'),
          this.$t('onpremise.resetPass.answerDiscord.disc'),
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
            this.$t('find.resetPassword.done.title'), // 비밀번호 변경 완료
            this.$t('find.resetPassword.done.message'), // 기존 로그인된 기기에서 로그아웃 됩니다. 변경된 새 비밀번호로 다시 로그인해 주세요.
            this.$t('login.accountError.btn'), // 확인
          )
        } else throw res
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
    async checkPass() {
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
