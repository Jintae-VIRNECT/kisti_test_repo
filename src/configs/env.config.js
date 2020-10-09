export let TARGET_COMPANY = 'VIRNECT'
export let USE_OPENROOM = false
export let USE_TRANSLATE = false

export const setConfigs = configs => {
  TARGET_COMPANY = configs.targetCompany
  USE_OPENROOM = configs.useOpenRoom
  USE_TRANSLATE = configs.useTranslate
}

export default {
  TARGET_COMPANY,
  USE_OPENROOM,
  USE_TRANSLATE,
}
