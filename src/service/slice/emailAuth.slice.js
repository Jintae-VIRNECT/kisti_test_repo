import { ref } from '@vue/composition-api'
import AuthService from 'service/auth-service'
import { alertMessage } from 'mixins/alert'

const errorMessage = (e, root) => {
  if (e.code === 2200)
    return alertMessage(
      root,
      root.$t('signup.errors.duplicateEmail.title'), // 이메일 인증 메일 전송 실패
      root.$t('signup.errors.duplicateEmail.contents'), // 이미 VIRNECT 회원으로 등록된 이메일 주소입니다.
      'error',
    )
  else if (e.code === 2206)
    return alertMessage(
      root,
      root.$t('signup.errors.secessionEmail.title'), // 이메일 인증 메일 전송 실패
      root.$t('signup.errors.secessionEmail.contents'), // 탈퇴한 유저입니다.
      'error',
    )
  else
    return alertMessage(
      root,
      root.$t('signup.errors.verification.title'), // 이메일 인증 메일 전송 실패
      root.$t('signup.errors.verification.contents'), // 인증 메일 전송에 실패하였습니다. 잠시 후 다시 시도해 주세요.
      'error',
    )
}

export default function emailAuth(root, signup, check) {
  const isVerification = ref(false)
  const authLoading = ref(false)
  const setCount = ref(false)
  const isValidEmail = ref(false)
  const verificationCode = ref('')
  const verificationText = ref('signup.authentication.verification')

  const delayResend = () => {
    setCount.value = false
    setTimeout(() => {
      setCount.value = true
    }, 10000)
  }

  const checkVerificationCode = async () => {
    const code = {
      code: verificationCode.value,
      email: signup.value.email,
    }
    try {
      const res = await AuthService.verification({ params: code })
      if (res.code === 200) {
        signup.value.sessionCode = res.data.sessionCode
        isVerification.value = false
        isValidEmail.value = false
        verificationText.value = 'signup.authentication.done'
        check.value = true
        alertMessage(
          root,
          root.$t('signup.authentication.message.done.title'), // 이메일 인증 성공
          root.$t('signup.authentication.message.done.contents'), // 이메일 인증이 완료되었습니다.
          'success',
        )
      } else throw res
    } catch (e) {
      if (e.code === 2201)
        return alertMessage(
          root,
          root.$t('signup.authentication.message.errors.title'), //인증 번호 불일치
          root.$t('signup.authentication.message.errors.contents'), // 인증 번호가 일치하지 않습니다. 다시 확인하여 입력해 주세요.
          'error',
        )
      else
        return alertMessage(
          root,
          root.$t('signup.authentication.message.errors.fail'), // 이메일 인증 실패
          root.$t('signup.authentication.message.errors.failContent'), // 이메일 인증에 실패하였습니다. 잠시 후 다시 시도해 주세요.
          'error',
        )
    }
    delayResend()
  }

  const sendEmail = async () => {
    try {
      const params = {
        email: signup.value.email,
      }
      const mailAuth = await AuthService.emailAuth(params)

      if (mailAuth.code === 200) {
        authLoading.value = true
        isVerification.value = true
        delayResend()
        alertMessage(
          root,
          root.$t('signup.done.verification.title'), // 이메일 인증 메일 전송 성공
          root.$t('signup.done.verification.contents'), // 입력하신 이메일로 인증 메일을 전송했습니다. 인증 메일의 인증 번호를 확인하여 입력해 주세요.
          'success',
        )
      } else throw mailAuth
    } catch (e) {
      errorMessage(e, root)
    }
  }
  return {
    isVerification,
    authLoading,
    setCount,
    isValidEmail,
    verificationCode,
    verificationText,
    sendEmail,
    checkVerificationCode,
  }
}
