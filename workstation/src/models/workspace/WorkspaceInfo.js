import Model from '@/models/Model'
import Workspace from '@/models/workspace/Workspace'
import Member from '@/models/workspace/Member'

export default class WorkspaceInfo extends Model {
  /**
   * 워크스페이스 홈화면 정보 구조
   * @param {Object} json
   */
  constructor(json = {}) {
    super()
    const members = json.workspaceUserInfo.map(user => new Member(user))
    return {
      info: new Workspace(json.workspaceInfo),
      master: members.find(member => member.role === 'MASTER'),
      managers: members.filter(member => member.role === 'MANAGER'),
      members: members.filter(member => member.role === 'MEMBER'),
      plansCount: {
        remote: json.remotePlanCount,
        make: json.makePlanCount,
        view: json.viewPlanCount,
      },
    }
  }
}
