import {
  ref,
  computed,
  watch,
  onMounted,
  onUnmounted,
} from '@vue/composition-api'
import dayjs from 'dayjs'
import { alertMessage } from 'mixins/alert'
import {
  emailValidate,
  userBirth,
  validBirth,
  passValidate,
} from 'mixins/validate'
import emailAuth from 'service/slice/emailAuth.slice'

const createI18nArray = (root, i18nArrayKey) => {
  const arr = []
  for (let i in root.$t(i18nArrayKey)) {
    arr.push({
      label: `${i18nArrayKey}[${Number(i)}]`,
      value: Number(i),
    })
  }
  return arr
}

const eventPrevent = e => {
  e.preventDefault()
  e.returnValue = ''
}

export default function terms(props, root, signup) {
  const birth = ref({
    year: '',
    month: '',
    date: '',
  })
  const timeSet = ref(dayjs(new Date()))
  const joinInfo = ref('')
  const serviceInfo = ref('')
  const check = ref(false)
  const subscriptionPath = createI18nArray(root, 'signup.subscriptionPathLists')
  const serviceInfoLists = createI18nArray(root, 'signup.serviceInfoLists')

  const EMAIL_AUTH_SLICE = emailAuth(root, signup, check)

  const watchInput = (val, key) => {
    signup.value[key] = val
  }

  const checkSignup = () => {
    // 폼내용전송
    setTimeout(() => {
      window.scrollTo({
        left: 0,
        top: 0,
      })
    }, 400)
    root.$router.push({
      name: 'user',
      params: { signup: signup.value },
    })
  }

  const checkAge = async () => {
    signup.value.birth = userBirth(birth.value)
    if (!validBirth(birth.value, signup.value.birth)) {
      alertMessage(root, 'Error', 'Invalid Date', 'error')
      return false
    }
    let today = dayjs()
    let userAge = dayjs(signup.value.birth)
    try {
      let age = (await today.format('YYYY')) - userAge.format('YYYY')
      if (age > 14) {
        checkSignup()
        return true
      } else throw false
    } catch (e) {
      alertMessage(
        root,
        root.$t('signup.errors.ageLimit.title'),
        root.$t('signup.errors.ageLimit.contents'),
        'error',
      )
    }
  }

  const resetJoinInfo = val => {
    if (val === subscriptionPath.length - 1) {
      signup.value.joinInfo = ''
    } else {
      const info = subscriptionPath[val]
      signup.value.joinInfo = root.$t(info.label)
    }
  }

  const resetServiceInfo = val => {
    if (val === serviceInfoLists.length - 1) {
      signup.value.serviceInfo = ''
    } else {
      const info = serviceInfoLists[val]
      signup.value.serviceInfo = root.$t(info.label)
    }
  }

  const notVirnectEmailCheck = () => {
    const { email, inviteSession } = root.$route.query
    if (email && inviteSession) {
      signup.value.email = email
      signup.value.inviteSession = inviteSession
    }
  }
  const emailValid = computed(() => {
    return emailValidate(signup.value.email)
  })

  const nextBtn = computed(() => {
    if (!check.value) return false
    if (
      signup.value.password !== signup.value.passwordConfirm &&
      signup.value.passwordConfirm !== ''
    )
      return false
    if (!passValidate(signup.value.password)) return false
    if (signup.value.lastName === '' || signup.value.firstName === '')
      return false

    if (
      /(\s)/g.test(signup.value.lastName) &&
      /(\s)/g.test(signup.value.firstName)
    )
      return false

    if (
      birth.value.year === '' ||
      birth.value.month === '' ||
      birth.value.date === ''
    )
      return false
    if (signup.value.joinInfo === '') return false

    if (signup.value.serviceInfo === '') return false
    return true
  })

  watch(
    () => root.$i18n.locale,
    () => {
      const info = subscriptionPath[joinInfo.value]
      signup.value.joinInfo =
        joinInfo.value === subscriptionPath.length - 1
          ? ''
          : root.$t(info.label)
      const service = serviceInfoLists[serviceInfo.value]
      signup.value.serviceInfo =
        serviceInfo.value === serviceInfoLists.length - 1
          ? ''
          : root.$t(service.label)
    },
  )
  watch(
    () => birth.value.year,
    newTime => {
      if (!newTime) return false
      const newYear = dayjs(newTime).year()
      timeSet.value = dayjs().year(newYear)
      birth.value.month = timeSet.value
      birth.value.date = timeSet.value
    },
  )
  watch(
    () => birth.value.month,
    newTime => {
      if (!newTime) return false
      const newYear = dayjs(timeSet.value).year()
      const newMonth = dayjs(newTime).month()
      timeSet.value = dayjs().year(newYear).month(newMonth)
      birth.value.date =
        birth.value.date && dayjs(timeSet.value).month(newMonth)
    },
  )
  watch(
    () => birth.value.date,
    newTime => {
      const newYear = dayjs(timeSet.value).year()
      const newMonth = dayjs(timeSet.value).month()
      const newDate = dayjs(newTime).date()

      timeSet.value = dayjs().year(newYear).month(newMonth).date(newDate)
    },
  )

  onMounted(() => {
    if (!props.policyAgree) {
      root.$router.push('/')
    }
    notVirnectEmailCheck()

    window.addEventListener('beforeunload', eventPrevent)
    if (props.marketInfoReceive.value)
      return (signup.value.marketInfoReceive = 'ACCEPT')
    else return (signup.value.marketInfoReceive = 'REJECT')
  })

  onUnmounted(() => {
    window.removeEventListener('beforeunload', eventPrevent)
  })

  return {
    signup,
    subscriptionPath,
    serviceInfoLists,
    birth,
    timeSet,
    check,
    joinInfo,
    serviceInfo,
    emailValid,
    nextBtn,
    watchInput,
    ...EMAIL_AUTH_SLICE,
    validBirth,
    checkAge,
    resetJoinInfo,
    resetServiceInfo,
  }
}
