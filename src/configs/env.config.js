export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 0 //'VIRNECT'
export let WHITE_LOGO = false
export let DEFAULT_LOGO = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  WHITE_LOGO = configs.whiteLogo
  DEFAULT_LOGO = configs.defaultLogo
}

export let URLS = {}
export const setUrls = urls => {
  URLS = {
    ...urls,
  }
}

export let SETTINGS = {}
export const setSettings = settings => {
  SETTINGS = {
    ...settings,
  }
}

export const RUNTIME = {
  LOCAL: 'local',
  DEVELOP: 'develop',
  STAGING: 'staging',
  PRODUCTION: 'production',
  ONPREMISE: 'onpremise',
}

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
