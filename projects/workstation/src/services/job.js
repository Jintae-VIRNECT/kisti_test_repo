import api from '@/api/gateway'
import Job from '@/models/Job'

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
    return data.jobs.map(job => new Job(job))
  },
}
