import { ref, computed, onMounted, onBeforeMount } from '@vue/composition-api'
import Cookies from 'js-cookie'
import AuthService from 'service/auth-service'
import { domainRegex } from 'mixins/validate'
import { alertMessage } from 'mixins/alert'

const cookieOption = (urls, expire) => {
  const isDomain = urls.domain
    ? urls.domain
    : location.hostname.replace(/.*?\./, '')

  const URL = domainRegex.test(location.hostname) ? isDomain : location.hostname
  if (expire)
    return {
      secure: true,
      sameSite: 'None',
      expires: expire / 3600000,
      domain: URL,
    }
  else
    return {
      secure: true,
      sameSite: 'None',
      domain: URL,
    }
}

export default function login(props, root) {
  const login = ref({
    email: '',
    password: '',
    rememberMe: null,
    autoLogin: null,
  })
  const loading = ref(false)
  const message = ref('')
  const errorCode = ref(null)
  const focusOut = ref()

  const isDisable = computed(() => {
    return (
      loading.value ||
      login.value.email == '' ||
      login.value.password == '' ||
      /\s/g.test(login.value.email) ||
      /\s/g.test(login.value.password) ||
      login.value.email.length < 5 ||
      login.value.password.length < 1
    )
  })

  const resetPasswordPath = computed(() => {
    if (root.$env !== 'onpremise')
      return {
        name: 'findTab',
        params: { findCategory: 'reset_password' },
      }
    else {
      return {
        name: 'reset_password',
      }
    }
  })

  const checkToken = async () => {
    const redirectTarget = root.$route.query.continue
    if (!props.auth.isLogin) return false

    if (redirectTarget) {
      location.href = /^https?:/.test(redirectTarget)
        ? redirectTarget
        : `//${redirectTarget}`
    } else if (props.auth.isLogin) {
      location.href = root.$urls['workstation']
    }
  }

  const redirection = async res => {
    let redirectTarget = root.$route.query.continue
    if (!res.passwordInitialized) {
      try {
        await root.$confirm(
          root.$t('onpremise.login.redirect.disc'),
          root.$t('onpremise.login.redirect.title'),
          {
            confirmButtonText: root.$t('onpremise.login.redirect.confirm'),
            cancelButtonText: root.$t('onpremise.login.redirect.cancel'),
          },
        )
        location.replace(root.$urls.account)
      } catch (e) {
        location.replace(root.$urls.workstation)
      }
    } else {
      if (redirectTarget) {
        location.href = /^https?:/.test(redirectTarget)
          ? redirectTarget
          : `//${redirectTarget}`
      } else {
        location.href = root.$urls['workstation']
      }
    }
  }

  const errorDialog = error => {
    const failCount = error.failCount || 0
    // 일반 에러
    if (error.code === 2000 && failCount < 2) {
      message.value =
        root.$env !== 'onpremise'
          ? root.$t('login.accountError.contents')
          : root.$t('onpremise.login.error')
    }
    // 2회 이상 실패
    else if (1 < failCount && failCount < 5) {
      root
        .$confirm(
          root.$tc('login.securityError.contents', failCount),
          root.$t('login.securityError.title'),
          {
            confirmButtonText: root.$t('login.resetPassword'),
            cancelButtonText: root.$t('login.accountError.btn'),
            dangerouslyUseHTMLString: true,
          },
        )
        .then(() => root.$router.push(resetPasswordPath.value))
    }
    // 계졍 잠김
    else if (error.code === 2002 || failCount === 5) {
      root
        .$confirm(
          root.$t('login.lockedError.contents'),
          error.code === 2002
            ? root.$t('login.lockedError.title')
            : root.$t('login.lockedError.changed'),
          {
            confirmButtonText: root.$t('login.resetPassword'),
            cancelButtonText: root.$t('login.accountError.btn'),
            dangerouslyUseHTMLString: true,
          },
        )
        .then(() => root.$router.push(resetPasswordPath.value))
    }
    // 기타
    else {
      alertMessage(
        root,
        root.$t('login.networkError.title'),
        root.$t('login.networkError.contents'),
        'error',
      )
      message.value = error.message
    }
  }

  const handleLogin = async () => {
    if (login.value.password.length < 1) return
    loading.value = true
    root.$validator.validateAll()
    focusOut.value.focus()
    try {
      let res = await AuthService.login({ params: login.value })
      if (res.code === 200) {
        Cookies.set(
          'accessToken',
          res.data.accessToken,
          cookieOption(root.$urls, res.data.expireIn),
        )
        Cookies.set(
          'refreshToken',
          res.data.refreshToken,
          cookieOption(root.$urls, res.data.expireIn),
        )

        redirection(res.data)
      } else throw res
    } catch (error) {
      loading.value = false
      errorCode.value = error.code
      errorDialog(error)
    }
  }

  const emailRemember = (email, bool) => {
    if (bool) Cookies.set('email', email, cookieOption(root.$urls))
    else Cookies.remove('email', cookieOption(root.$urls))
  }

  const autoLogin = bool => {
    if (bool) Cookies.set('auto', bool, cookieOption(root.$urls))
    else Cookies.remove('auto')
  }

  onBeforeMount(() => {
    checkToken()
  })

  onMounted(() => {
    if (Cookies.get('auto')) {
      login.value.autoLogin = true
    }
    if (Cookies.get('email')) {
      login.value.rememberMe = true
      login.value.email = Cookies.get('email')
    }
  })

  return {
    login,
    loading,
    message,
    errorCode,
    focusOut,
    isDisable,
    resetPasswordPath,
    checkToken,
    handleLogin,
    emailRemember,
    autoLogin,
  }
}
