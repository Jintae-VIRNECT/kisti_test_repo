export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 0 //'VIRNECT'
export let SETTING_LOGO = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  SETTING_LOGO = configs.settingLogo || SETTING_LOGO
}

export const RUNTIME = {
  LOCAL: 'local',
  DEVELOP: 'develop',
  STAGING: 'staging',
  PRODUCTION: 'production',
  ONPREMISE: 'onpremise',
}

export const COMPANY_CODE = {
  VIRNECT: 0,
  KINTEX: 1,
}

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
