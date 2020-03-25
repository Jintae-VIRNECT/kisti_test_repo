import api from '@/api/gateway'
import Issue from '@/models/job/Issue'

export default {
  /**
   * 전체 이슈 리스트 검색
   * @param {Object} params
   */
  async searchIssues(params = {}) {
    // filter => inout
    params.inout = params.filter
    delete params.filter

    const data = await api('ISSUE_LIST', {
      params: {
        size: 10,
        inout: 'ALL',
        searchType: 'USER_NAME',
        ...params,
      },
    })
    return {
      list: data.issues.map(issue => new Issue(issue)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   *  이슈 상세 정보
   * @param {String} issueId
   */
  async getIssueInfo(issueId) {
    const data = await api('ISSUE_INFO', {
      route: { issueId },
    })
    return new Issue(data)
  },
}
