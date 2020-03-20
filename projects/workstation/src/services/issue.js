import api from '@/api/gateway'
import { Issue } from '@/models/issue'

export default {
  /**
   * 전체 이슈 리스트 검색
   * @param {Object} params
   */
  async searchIssues(params) {
    const data = await api('ISSUE_LIST', {
      params: { ...params },
    })
    return data.issues.map(issue => Issue(issue))
  },
}
