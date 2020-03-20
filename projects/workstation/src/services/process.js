import api from '@/api/gateway'
import { Process, ProcessStatistics } from '@/models/process/process'

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
  /**
   * 공정 삭제
   * @param {String} contentId
   */
  async deleteProcess(processId) {
    return await api('PROCESS_DELETE', {
      route: { processId },
    })
  },
  /**
   * 공정 종료
   * @param {String} contentId
   */
  async closeProcess(processId) {
    return await api('PROCESS_CLOSE', {
      route: { processId },
    })
  },
  /**
   * 공정 종료
   * @param {String} contentId
   */
  async createProcess(form) {
    return await api('PROCESS_CREATE', {
      params: form,
    })
  },
}
