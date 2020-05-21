import { api } from '@/plugins/axios'
import Job from '@/models/job/Job'
import Report from '@/models/job/Report'
import SmartTool from '@/models/job/SmartTool'

export default {
  /**
   * 세부공정 하위 작업 리스트 검색
   * @param {String} subProcessId
   * @param {Object} params
   */
  async searchChildJobs(subProcessId, params) {
    const data = await api('JOBS_LIST', {
      route: { subProcessId },
      params: { ...params },
    })
    return {
      list: data.jobs.map(job => new Job(job)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   *  리포트 상세 정보
   * @param {String} reportId
   */
  async getReportInfo(reportId) {
    const data = await api('REPORT_INFO', {
      route: { reportId },
    })
    return new Report(data)
  },
  /**
   * 스마트툴 상세 정보
   * @param {String} subProcessId
   * @param {String} smartToolId
   */
  async getSmartToolInfo(subProcessId, smartToolId) {
    const data = await api('SMART_TOOL_LIST', {
      params: { subProcessId, smartToolId },
    })
    const smartTool = data.smartTools.find(
      smartTool => smartTool.smartToolId === smartToolId,
    )
    return new SmartTool(smartTool)
  },
}
