export let RUNTIME_ENV = 'production'
export let TIMEOUT = 5000
export let ALLOW_NO_AUDIO = false
export let ALLOW_NO_DEVICE = false
export let TARGET_COMPANY = 0 //'VIRNECT'
export let WHITE_LOGO = false
export let DEFAULT_LOGO = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.RUNTIME_ENV || RUNTIME_ENV
  TIMEOUT = configs.TIMEOUT || TIMEOUT
  ALLOW_NO_AUDIO = configs.ALLOW_NO_AUDIO || ALLOW_NO_AUDIO
  ALLOW_NO_DEVICE = configs.ALLOW_NO_DEVICE || ALLOW_NO_DEVICE
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  WHITE_LOGO = configs.whiteLogo || WHITE_LOGO
  DEFAULT_LOGO = configs.defaultLogo || DEFAULT_LOGO
}

export const RUNTIME = {
  LOCAL: 'local',
  DEVELOP: 'develop',
  STAGING: 'staging',
  PRODUCTION: 'production',
  ONPREMISE: 'onpremise',
}

export let URLS = {}
export const setUrls = urls => {
  URLS = {
    ...urls,
  }
}

export let RECORD_INFO = {}
export const setRecordInfo = info => {
  RECORD_INFO = info
}

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
