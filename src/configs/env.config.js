export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 0 //'VIRNECT'
export let WHITE_LOGO = false
export let DEFAULT_LOGO = false
export let TIMEOUT = 5000

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  WHITE_LOGO = configs.whiteLogo || WHITE_LOGO
  DEFAULT_LOGO = configs.defaultLogo || DEFAULT_LOGO
  TIMEOUT = configs.timeout || TIMEOUT
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
