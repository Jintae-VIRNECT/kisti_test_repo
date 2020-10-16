export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 0 //'VIRNECT'
export let OPEN_ROOM = false
export let USE_TRANSLATE = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
  OPEN_ROOM = configs.openRoom || OPEN_ROOM
  USE_TRANSLATE = configs.useTranslate || USE_TRANSLATE
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
  OPEN_ROOM,
  USE_TRANSLATE,
}
