import Vue from 'vue'
import VueI18n from 'vue-i18n'
import messages from 'languages/remote'

Vue.use(VueI18n)

const options = {
  locale: 'ko',
  fallbackLocale: 'en',
  messages: messages,
}
export default new VueI18n(options)
