import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'

// model
import OnPremiseSetting from '@/models/OnPremiseSetting'

function activeWorkspaceGetter() {
  return store.getters['auth/activeWorkspace']
}
function myProfileGetter() {
  return store.getters['auth/myProfile']
}

export default {
  /**
   * 워크스페이스 세팅 불러오기 (onpremise)
   */
  async getWorkspaceSetting() {
    const data = await api('WORKSPACE_GET_SETTING')
    return new OnPremiseSetting(data)
  },
  /**
   * 워크스페이스 타이틀 설정 (onpremise)
   * @param {string} title
   */
  async setWorkspaceTitle(title) {
    const workspaceId = activeWorkspaceGetter().uuid
    const data = await api('WORKSPACE_SET_TITLE', {
      route: { workspaceId },
      params: {
        title,
        workspaceId,
        userId: myProfileGetter().uuid,
      },
    })
    return data
  },
  /**
   * 워크스페이스 로고 설정 (onpremise)
   * @param {File} logo
   * @param {File} whiteLogo
   */
  async setWorkspaceLogo(logo, whiteLogo) {
    const formData = new FormData()
    formData.append('userId', myProfileGetter().uuid)
    if (logo) formData.append('defaultLogo', logo)
    if (whiteLogo) {
      formData.append('whiteLogo', whiteLogo)
      formData.append('greyLogo', whiteLogo)
    }

    const data = await api('WORKSPACE_SET_LOGO', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
    return data
  },
  /**
   * 워크스페이스 파비콘 설정 (onpremise)
   */
  async setWorkspaceFavicon(favicon) {
    const formData = new FormData()
    formData.append('userId', myProfileGetter().uuid)
    if (favicon) formData.append('favicon', favicon)

    const data = await api('WORKSPACE_SET_FAVICON', {
      route: { workspaceId: activeWorkspaceGetter().uuid },
      params: formData,
    })
    return data
  },
}
