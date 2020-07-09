import { getLicense } from 'api/workspace/license'

//콜백 함수 실행

export const checkLicense = async (workspaceId, userId, noLicenseCallback) => {
  const licenseCheck = await getLicense(workspaceId, userId)

  if (noLicenseCallback && typeof noLicenseCallback === 'function') {
    if (!licenseCheck) {
      noLicenseCallback()
    }
  }

  return licenseCheck
}
