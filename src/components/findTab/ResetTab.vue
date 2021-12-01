<template>
  <div class="find-body">
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
        name="authcode"
        v-if="isCodeAuth === false"
      ></el-input>

      <p class="input-title must-check" v-if="isCodeAuth === true">
        {{ $t('login.password') }}
      </p>
      <el-input
        :placeholder="$t('find.authInput.newPass')"
        type="password"
        name="password"
        clearable
        v-model="resetPass.password"
        v-if="isCodeAuth === true"
        :class="{ 'input-danger': errors.has('password') }"
      ></el-input>
      <el-input
        :placeholder="$t('find.authInput.newPassConfirm')"
        type="password"
        v-model="resetPass.comfirmPassword"
        v-if="isCodeAuth === true"
        name="passwordConfirm"
        clearable
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
</template>

<script>
import { ref, computed } from '@vue/composition-api'
import UserService from 'service/user-service'
import { alertMessage, confirmWindow } from 'mixins/alert'
import { passValidate, emailValidate } from 'mixins/validate'

export default {
  setup(props, { root }) {
    const isCodeAuth = ref(null)
    const resetPass = ref({
      email: '',
      authCode: '',
      password: '',
      comfirmPassword: '',
    })

    const emailPassCode = async () => {
      // 비밀번호 재설정 이메일 확인
      try {
        let res = await UserService.userPass({
          params: { email: resetPass.value.email },
        })
        if (res.code === 200) {
          alertMessage(
            root,
            root.$t('find.authCode.done.send.title'), // 보안코드 전송
            root.$t('find.authCode.done.send.message'), // 가입하신 이메일로 보안 코드가 전송되었습니다. 이메일의 보안 코드를 확인하여 입력해 주세요.
            'success',
          )
          isCodeAuth.value = false
        } else {
          throw res
        }
      } catch (e) {
        // console.log(e.code)
        if (e.code === 4002)
          return alertMessage(
            root,
            root.$t('find.authCode.error.send'), // 보안코드 전송 오류
            root.$t('find.authCode.error.nobody'), // 입력하신 정보와 일치하는 VIRNECT 회원이 없습니다.
            'error',
          )
        else if (e.code === 9999)
          return alertMessage(
            root,
            root.$t('find.authCode.error.send'), // 보안코드 전송 오류
            root.$t('find.authCode.error.timeOut'),
            // 보안 코드는 보안 유지를 위해 발급 후 1분 내에 재발송 되지 않습니다. 계정의 메일함을 확인해 주시거나, 잠시 후 다시 이용해 주세요
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.authCode.error.send'), // 보안코드 전송 오류
            root.$t('find.authCode.error.etc'), // 보안코드 이메일 전송에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    }
    const userId = ref(null)
    const userEmail = ref(null)

    const authCodeCheck = async () => {
      try {
        let res = await UserService.userCodeCheck({
          params: {
            code: resetPass.value.authCode,
            email: resetPass.value.email,
          },
        })
        if (res.code === 200) {
          userId.value = res.data.uuid
          userEmail.value = res.data.email
          alertMessage(
            root,
            root.$t('find.authCode.done.sync.title'), // 보안코드 일치
            root.$t('find.authCode.done.sync.message'), // 보안 코드 인증이 완료되었습니다.
            'success',
          )
          isCodeAuth.value = true
        } else {
          throw res
        }
      } catch (e) {
        // console.log(e.code)
        if (e.code === 4007)
          return alertMessage(
            root,
            root.$t('find.authCode.error.notSyncCode'), // 보안코드 불일치 오류
            root.$t('find.authCode.error.sendMessage'), // 입력하신 보안 코드가 일치하지 않습니다. 다시 한 번 확인해 주세요.
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.authCode.error.auth'), // 보안코드 인증 오류
            root.$t('find.authCode.error.etc'), // 보안 코드 인증에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    }

    const emailValid = computed(() => {
      return emailValidate(resetPass.value.email)
    })
    const authText = computed(() => {
      if (isCodeAuth.value) return root.$t('find.authText.after')
      else return root.$t('find.authText.before')
    })

    const changePass = async () => {
      const params = {
        uuid: userId.value,
        email: userEmail.value,
        password: resetPass.value.password,
      }
      try {
        const res = await UserService.userPassChange({ params: params })
        // console.log(res)
        if (res.code === 200)
          return confirmWindow(
            root,
            root.$t('find.resetPassword.done.title'), // 비밀번호 변경 완료
            root.$t('find.resetPassword.done.message'), // 기존 로그인된 기기에서 로그아웃 됩니다. 변경된 새 비밀번호로 다시 로그인해 주세요.
            root.$t('login.accountError.btn'), // 확인
          )
        else throw res
      } catch (e) {
        if (e.code === 4009)
          return alertMessage(
            root,
            root.$t('find.resetPassword.error.title'), // 비밀번호 재설정 실패
            root.$t('find.resetPassword.error.multiple'), // 이전과 동일한 비밀번호는 새 비밀번호로 설정할 수 없습니다.
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.resetPassword.error.title'), // 비밀번호 재설정 실패
            root.$t('find.resetPassword.error.etc'), // 비밀번호 변경에 실패하였습니다. 잠시 후 다시 이용해 주세요.
            'error',
          )
      }
    }
    const checkPass = () => {
      if (passValidate(resetPass.value.password)) {
        changePass()
      } else {
        alertMessage(
          root,
          root.$t('find.authCode.error.rulePass'), // 비밀번호 입력 오류
          root.$t('find.authCode.error.rulePassMessage'),
          // 비밀번호는 8~20자 이내로 영문 대,소문자/숫자/특수문자( . , !, @, #, $, % )를 3가지 이상 조합하여 입력해 주세요. 연속된 숫자 또는 4자 이상의 동일 문자는 비밀번호로 사용할 수 없습니다.
          'error',
        )
      }
    }

    // onMounted(() => {
    //   root.$validator.extend('password', {
    //     getMessage: () => root.$t('signup.password.notice'),
    //     validate: value => passValidate(value),
    //   })
    // })

    return {
      isCodeAuth,
      resetPass,
      emailPassCode,
      authText,
      emailValid,
      authCodeCheck,
      checkPass,
    }
  },
}
</script>
