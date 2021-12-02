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
        :class="{
          'input-danger': !emailValid && resetPass.email !== '',
        }"
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

      <PassWordConfirm
        v-if="isCodeAuth"
        :pass="resetPass"
        :setString="setString"
        @watchInput="watchInput"
      />

      <el-button
        class="next-btn block-btn"
        type="primary"
        :disabled="resetPass.authCode.length < 6"
        @click="authCodeCheck"
        v-if="!isCodeAuth"
        >{{ $t('find.authInput.code') }}</el-button
      >
      <el-button
        class="next-btn block-btn"
        type="primary"
        :disabled="resetPass.password === passwordConfirm"
        @click="checkPass"
        v-if="isCodeAuth"
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
import PassWordConfirm from 'components/PassWordConfirm'

export default {
  components: { PassWordConfirm },
  setup(props, { root }) {
    const isCodeAuth = ref(null)
    const resetPass = ref({
      email: '',
      authCode: '',
      password: '',
    })
    const passwordConfirm = ref('')

    const emailPassCode = async () => {
      // 비밀번호 재설정 이메일 확인
      try {
        let res = await UserService.userPass({
          params: { email: resetPass.value.email },
        })
        if (res.code === 200) {
          alertMessage(
            root,
            root.$t('find.authCode.done.send.title'),
            root.$t('find.authCode.done.send.message'),
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
            root.$t('find.authCode.error.send'),
            root.$t('find.authCode.error.nobody'),
            'error',
          )
        else if (e.code === 9999)
          return alertMessage(
            root,
            root.$t('find.authCode.error.send'),
            root.$t('find.authCode.error.timeOut'),
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.authCode.error.send'),
            root.$t('find.authCode.error.etc'),
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
            root.$t('find.authCode.done.sync.title'),
            root.$t('find.authCode.done.sync.message'),
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
            root.$t('find.authCode.error.notSyncCode'),
            root.$t('find.authCode.error.sendMessage'),
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.authCode.error.auth'),
            root.$t('find.authCode.error.etc'),
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
            root.$t('find.resetPassword.done.title'),
            root.$t('find.resetPassword.done.message'),
            root.$t('login.accountError.btn'), // 확인
          )
        else throw res
      } catch (e) {
        if (e.code === 4009)
          return alertMessage(
            root,
            root.$t('find.resetPassword.error.title'),
            root.$t('find.resetPassword.error.multiple'),
            'error',
          )
        else
          return alertMessage(
            root,
            root.$t('find.resetPassword.error.title'),
            root.$t('find.resetPassword.error.etc'),
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
          root.$t('find.authCode.error.rulePass'),
          root.$t('find.authCode.error.rulePassMessage'),
          'error',
        )
      }
    }
    const setString = {
      password: root.$t('find.authInput.newPass'),
      passWordConfirm: root.$t('find.authInput.newPassConfirm'),
    }
    const watchInput = (val, key) => {
      resetPass.value[key] = val
    }
    return {
      isCodeAuth,
      resetPass,
      passwordConfirm,
      emailPassCode,
      authText,
      emailValid,
      authCodeCheck,
      checkPass,
      setString,
      watchInput,
    }
  },
}
</script>
