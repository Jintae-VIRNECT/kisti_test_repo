import ko from './ko'
import elementKo from 'element-ui/lib/locale/lang/ko'

module.exports = {
  locales: ['ko'],
  defaultLocale: 'ko',
  vueI18n: {
    fallbackLocale: 'ko',
    messages: {
      ko: { ...elementKo, ...ko },
    },
  },
}
