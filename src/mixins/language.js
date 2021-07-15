import Cookies from 'js-cookie'

const order = ['en', 'ko', 'ja', 'zh-CN', 'zh-TW']
const shortLang = new Map([
  ['en', 'en'],
  ['en-us', 'en'],
  ['ko', 'ko'],
  ['ko-kr', 'ko'],
  ['ja', 'ja'],
  // ['zh-CN', 'zh-CN'],
  // ['zh-TW', 'zh-TW'],
])

const cookieOption = {
  domain:
    location.hostname.split('.').length === 3
      ? location.hostname.replace(/.*?\./, '')
      : location.hostname,
}
const setLangCookie = locale => {
  Cookies.set('lang', locale, cookieOption)
}

//언어설정
export default {
  computed: {
    currentLanguage() {
      return this.$i18n.locale
    },
    languages() {
      return this.$i18n.availableLocales.sort(function (langA, langB) {
        return order.indexOf(langA) - order.indexOf(langB)
      })
    },
  },
  methods: {
    mx_initLang() {
      const locale = this.mx_getLangCode()
      setLangCookie(locale)
      document.documentElement.lang = locale
      this.$i18n.locale = locale
      if (this.$dayjs) {
        this.$dayjs.locale(locale)
      }
    },

    mx_getLangCode() {
      let lang = Cookies.get('lang')

      if (!lang) {
        lang =
          navigator.language ||
          navigator.userLanguage || //for IE
          navigator.systemLanguage || //for IE
          'en'
      }

      return shortLang.get(lang.toLowerCase())
    },

    mx_changeLang(locale) {
      if (locale) {
        setLangCookie(locale)
      } else {
        locale = this.mx_getLangCode()
        setLangCookie(locale)
      }

      this.$i18n.locale = locale
      document.documentElement.lang = locale
      this.$dayjs.locale(locale)

      if (!this.account || !this.account.sId) {
        return
      }
    },
  },
}
