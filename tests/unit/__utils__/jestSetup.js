import { config } from '@vue/test-utils'
import i18n from './i18n'

config.mocks['$t'] = key => i18n.t(key)
