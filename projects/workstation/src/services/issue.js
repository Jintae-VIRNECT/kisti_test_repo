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
  /**
   *  이슈 상세 정보
   * @param {String} issueId
   */
  async getIssueInfo(issueId) {
    const data = await api('ISSUE_INFO', {
      route: { issueId },
    })
    return Issue(data)
  },
}
