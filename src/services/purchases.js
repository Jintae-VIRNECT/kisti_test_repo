import { api } from '@/plugins/axios'
import PlanMember from '@/models/purchases/PlanMember'
import PlansInfo from '@/models/purchases/PlansInfo'
import workspaceService from '@/services/workspace'

export default {
  /**
   * 라이센스 멤버 검색
   * @param {Object} searchParams
   */
  async searchPlanMembers(searchParams = {}) {
    const { workspaceUserLicenseInfoList, pageMeta } = await api(
      'GET_LICENSE_MEMBERS',
      {
        route: { workspaceId: workspaceService.getMasterWorkspaceInfo().uuid },
        params: {
          sort: 'plan,desc',
          size: 8,
          ...searchParams,
        },
      },
    )
    return {
      list: workspaceUserLicenseInfoList.map(member => new PlanMember(member)),
      total: pageMeta.totalElements,
    }
  },
  /**
   * 워크스페이스 플랜 정보 조회
   */
  async getWorkspacePlansInfo() {
    const data = await api('GET_WORKSPACE_PLAN_INFO', {
      route: { workspaceId: workspaceService.getMasterWorkspaceInfo().uuid },
    })
    return new PlansInfo(data)
  },
}
