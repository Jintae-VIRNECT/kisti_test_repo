import Vue from 'vue/dist/vue.js'
import VueI18n from 'vue-i18n'
const lang = require('@/languages')

Vue.use(VueI18n)
lang.vueI18n['fallbackLocale'] = 'ko'
lang.vueI18n['locale'] = 'ko'
lang.vueI18n['silentTranslationWarn'] = true

const i18n = new VueI18n(lang.vueI18n)
export default i18n
