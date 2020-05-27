import { localStorage } from 'utils/storage'
// import $http from 'api/service/gateway'

const order = ['ko', 'en', 'zh']
const shortLang = new Map([
  ['en', 'en'],
  ['en-us', 'en'],
  ['ko', 'ko'],
  ['ko-kr', 'ko'],
  ['zh', 'zh'],
  ['zh-cn', 'zh'],
  ['zh-hk', 'zh'],
  ['zh-tw', 'zh'],
])

function getLanguageCode() {
  let langCode = localStorage.getItem('lang')

  if (false == !!langCode) {
    langCode =
      navigator.language ||
      navigator.userLanguage ||
      navigator.systemLanguage ||
      'en'
  }

  return shortLang.get(langCode.toLowerCase())
}

//언어설정
export default {
  computed: {
    mx_currentLanguage() {
      return this.$i18n.locale
    },
    mx_languages() {
      return this.$i18n.availableLocales.sort(function(langA, langB) {
        return order.indexOf(langA) - order.indexOf(langB)
      })
    },
  },
  methods: {
    mx_changeLanguage(locale) {
      if (this.isScreenApp) {
        this.mx_mobileChangeLanguage(locale)
        return
      }

      if (locale) {
        localStorage.setItem('lang', locale)
      } else {
        locale = getLanguageCode()
        localStorage.setItem('lang', locale)
      }

      this.$i18n.locale = locale
      document.documentElement.lang = locale
      // this.$moment && this.$moment.locale('zh' === locale ? 'zh-cn' : locale)
      if (!this.account || !this.account.sId) return
      let params = {
        sId: this.account.sId,
        language: locale,
      }
      $http('SET_LANGUAGE', params).then(res => {})
    },
    mx_mobileChangeLanguage(locale) {
      if (!locale) {
        locale = getLanguageCode()
      }
      this.$i18n.locale = locale
      document.documentElement.lang = locale
    },
  },
}
