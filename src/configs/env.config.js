export let RUNTIME_ENV = 'production'
export let TARGET_COMPANY = 'VIRNECT'

export const setConfigs = configs => {
  RUNTIME_ENV = configs.runtimeEnv || RUNTIME_ENV
  TARGET_COMPANY = configs.targetCompany || TARGET_COMPANY
}

export default {
  RUNTIME_ENV,
  TARGET_COMPANY,
}
