// import { localStorage } from 'utils/storage'
// import $http from 'api/service/gateway'

const order = ['en', 'ko']
const shortLang = new Map([
  ['en', 'en'],
  ['en-us', 'en'],
  ['ko', 'ko'],
  ['ko-kr', 'ko'],
])

//언어설정
export default {
  computed: {
    currentLanguage() {
      return this.$i18n.locale
    },
    languages() {
      return this.$i18n.availableLocales.sort(function(langA, langB) {
        return order.indexOf(langA) - order.indexOf(langB)
      })
    },
  },
  methods: {
    mx_initLang() {
      const langCode = this.mx_getLangCode()
      localStorage.setItem('language', langCode)
      document.documentElement.lang = langCode
    },

    mx_getLangCode() {
      let langCode = localStorage.getItem('language')

      if (!langCode) {
        langCode =
          navigator.language ||
          navigator.userLanguage || //for IE
          navigator.systemLanguage || //for IE
          'en'
      }

      return shortLang.get(langCode.toLowerCase())
    },

    mx_changeLang(locale) {
      if (this.isScreenApp) {
        this.mx_mobileChangeLang(locale)
        return
      }

      if (locale) {
        localStorage.setItem('language', locale)
      } else {
        locale = this.mx_getLangCode()
        localStorage.setItem('language', locale)
      }

      this.$i18n.locale = locale
      document.documentElement.lang = locale

      if (!this.account || !this.account.sId) {
        return
      }
    },
    mx_mobileChangeLang(locale) {
      if (!locale) {
        locale = this.mx_getLangCode()
      }
      this.$i18n.locale = locale
      document.documentElement.lang = locale
    },
  },
}
