import Vue from 'vue'
import VueI18n from 'vue-i18n'
import store from '@/store'

import EN from '@/i18n/en'
import KO from '@/i18n/ko'

Vue.use(VueI18n)

const messages = {
	en: EN,
	ko: KO,
}
console.log(messages)
export default new VueI18n({
	locale: store.getters.getLocale || 'ko',
	fallbackLocale: 'en',
	messages,
})
