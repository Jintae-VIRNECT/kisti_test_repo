import { api } from '@/plugins/axios'
import SubTask from '@/models/task/SubTask'
import Issue from '@/models/result/Issue'
import Paper from '@/models/result/Paper'

export default {
  /**
   * 최근 보고된 하위 작업
   * @param {object} params
   */
  async searchCurrentSubTasks(params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('SUB_TASK_ALL', {
      params: {
        size: 10,
        sort: 'reportedDate,desc',
        ...params,
      },
    })
    return {
      list: data.subTasks.map(subTask => new SubTask(subTask)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 이슈 리스트
   * @param {object} params
   */
  async searchIssues(params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('ISSUES_ALL', {
      params: {
        size: 10,
        sort: 'updated_at,desc',
        ...params,
      },
    })
    return {
      list: data.issues.map(issue => new Issue(issue)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 페이퍼 리스트
   * @param {object} params
   */
  async searchPapers(params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('PAPERS_ALL', {
      params: {
        size: 10,
        sort: 'reported_date,desc',
        ...params,
      },
    })
    return {
      list: data.papers.map(paper => new Paper(paper)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 이슈 상세정보 조회
   * @param {String} issueId
   */
  async getIssueDetail(issueId) {
    const data = await api('ISSUE_INFO', {
      route: { issueId },
    })
    return new Issue(data)
  },
  /**
   * 페이퍼 상세정보 조회
   * @param {String} paperId
   */
  async getPaperDetail(paperId) {
    const data = await api('PAPER_INFO', {
      route: { paperId },
    })
    return new Paper(data)
  },
}
