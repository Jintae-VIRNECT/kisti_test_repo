import api from '@/api/gateway'
import { Process, ProcessStatistics } from '@/models/process'

export default {
  /**
   * 공정 통계
   */
  async getProcessStatistics() {
    const data = await api('PROCESS_STATISTICS')
    return ProcessStatistics(data)
  },
  /**
   * 공정 검색
   * @param {Object} params
   */
  async searchProcesses(params) {
    const data = await api('PROCESS_LIST', {
      params: {
        size: 10,
        ...params,
      },
    })
    return data.processes.map(process => Process(process))
  },
  /**
   * 공정 상세 정보
   * @param {String} processId
   */
  async getProcessInfo(processId) {
    const data = await api('PROCESS_INFO', {
      route: { processId },
    })
    return Process(data)
  },
}
