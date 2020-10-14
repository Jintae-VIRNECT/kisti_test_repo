export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 'VIRNECT'
export let OPEN_ROOM = false
export let USE_TRANSLATE = false

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
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
  OPEN_ROOM,
  USE_TRANSLATE,
}
