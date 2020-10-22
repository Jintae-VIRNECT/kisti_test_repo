export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 0 //'VIRNECT'
export let WHITE_LOGO = false
export let DEFAULT_LOGO = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
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

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
