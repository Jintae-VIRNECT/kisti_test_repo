import { store } from '@/plugins/context'
import { api } from '@/plugins/axios'
import SubTask from '@/models/task/SubTask'
import Issue from '@/models/result/Issue'
import Paper from '@/models/result/Paper'
import Trouble from '@/models/result/Trouble'

function activeWorkspaceGetter() {
  return store.getters['workspace/activeWorkspace']
}

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
        workspaceUUID: activeWorkspaceGetter().uuid,
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
        workspaceUUID: activeWorkspaceGetter().uuid,
        size: 10,
        sort: 'updatedDate,desc',
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
        workspaceUUID: activeWorkspaceGetter().uuid,
        size: 10,
        sort: 'updatedDate,desc',
        ...params,
      },
    })
    return {
      list: data.reports.map(paper => new Paper(paper)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 트러블 리스트
   * @param {object} params
   */
  async searchTroubles(params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('TROUBLES_LIST', {
      params: {
        workspaceUUID: activeWorkspaceGetter().uuid,
        size: 10,
        sort: 'updatedDate,desc',
        ...params,
      },
    })
    return {
      list: data.issues.map(trouble => new Trouble(trouble)),
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
  /**
   * 트러블메모 상세정보 조회
   * @param {String} troubleMemoId
   */
  async getTroubleDetail(troubleMemoId) {
    const data = await api('TROUBLE_INFO', {
      route: { troubleMemoId },
    })
    return new Trouble(data)
  },
}
