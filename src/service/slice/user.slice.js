import { ref, computed, onMounted } from '@vue/composition-api'
import { countryCode } from 'model/countryCode'
import {
  nickNameValidate,
  emailValidate,
  mobileNumberValidate,
} from 'mixins/validate'
import { cookieOption } from 'mixins/cookie'
import { alertMessage } from 'mixins/alert'
import Cookies from 'js-cookie'
import AuthService from 'service/auth-service'
import imageUpload from 'service/slice/imageUpload.slice'

const formDataSet = props => {
  const data = new FormData()

  Object.entries(props).map(([key, value]) => {
    data.append(key, value)
  })
  return data
}

const signUps = async (root, formData) => {
  try {
    let res = await AuthService.signUp({ params: formData })
    if (res.code === 200) {
      setTimeout(() => {
        window.scrollTo({
          left: 0,
          top: 0,
        })
      }, 400)
      root.$router.push({
        name: 'complete',
      })
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
    } else throw res
  } catch (e) {
    alertMessage(
      root,
      root.$t('user.error.etc.title'),
      root.$t('user.error.etc.message'),
      'error',
    )
  }
}

export default function user(props, root) {
  const isProfilePopup = ref(false)
  const user = ref({
    profile: '',
    nickname: '',
    mobile: '',
    recoveryEmail: '',
  })
  const userCountryCode = ref('+82')
  const countryCodeLists = countryCode
  const formData = ref(new FormData())
  const thumbnail = ref(null)
  const nicknameCheck = ref(true)
  const IMAGE_UPLOAD_SLICE = imageUpload(
    root,
    thumbnail,
    formData,
    isProfilePopup,
    user,
  )

  const nicknameSet = computed(() => {
    return `${props.signup.lastName}${props.signup.firstName}`
  })

  const mobileSet = computed(() => {
    return `${userCountryCode.value}-${user.value.mobile.replace(
      /[^0-9+]/g,
      '',
    )}`
  })

  const checkConfirm = computed(() => {
    if (user.value.nickname !== '') return true
    if (mobileNumberValidate(user.value.mobile)) return true
    if (user.value.profile !== '') return true
    if (emailValidate(user.value.recoveryEmail)) return true
    return false
  })

  const handleRegisterDetail = async () => {
    const formDataParams = formDataSet(props.signup)
    let mergeData = new FormData()
    for (let t of formDataParams.entries()) {
      mergeData.append(t[0], t[1])
    }
    if (user.value.profile !== '') {
      mergeData.append('profile', user.value.profile)
    }
    if (user.value.nickname !== '') {
      mergeData.append('nickname', user.value.nickname)
    } else {
      mergeData.append('nickname', nicknameSet.value)
    }
    if (user.value.mobile !== '') {
      mergeData.append('mobile', mobileSet.value)
    }
    if (user.value.recoveryEmail !== '') {
      mergeData.append('recoveryEmail', user.value.recoveryEmail)
    }

    // form data 확인용
    // for (let t of mergeData.entries()) {
    //   console.log(`${t[0]}: ${t[1]}`)
    // }

    signUps(root, mergeData)
  }

  const later = async () => {
    const formDataParams = formDataSet(props.signup)
    signUps(root, formDataParams)
  }

  const checkNickName = async () => {
    try {
      let res = nickNameValidate(user.value.nickname)
      if (res === true) {
        handleRegisterDetail()
      } else throw res
    } catch (e) {
      alertMessage(
        root,
        root.$t('user.error.nickName.title'),
        root.$t('user.error.nickName.message'),
        'error',
      )
    }
  }

  onMounted(() => {
    if (!props.signup) {
      root.$router.push('/')
    }
    root.$validator.extend('nickname', {
      validate: nickname => {
        nicknameCheck.value = nickNameValidate(nickname)
        return nicknameCheck.value
      },
    })
  })

  return {
    isProfilePopup,
    user,
    userCountryCode,
    countryCodeLists,
    formData,
    thumbnail,
    nicknameCheck,
    ...IMAGE_UPLOAD_SLICE,
    nicknameSet,
    mobileSet,
    checkConfirm,
    handleRegisterDetail,
    later,
    checkNickName,
  }
}
