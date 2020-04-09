import api from '@/api/gateway'
import Process from '@/models/process/Process'
import ProcessStatistics from '@/models/process/ProcessStatistics'

export default {
  /**
   * 공정 통계
   */
  async getProcessStatistics() {
    const data = await api('PROCESS_STATISTICS')
    return new ProcessStatistics(data)
  },
  /**
   * 공정 진행률
   */
  async getTotalRate() {
    const data = await api('PROCESS_TOTAL_RATE')
    return data.totalRate
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
    return {
      list: data.processes.map(process => new Process(process)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 공정 상세 정보
   * @param {String} processId
   */
  async getProcessInfo(processId) {
    const data = await api('PROCESS_INFO', {
      route: { processId },
    })
    return new Process(data)
  },
  /**
   * 공정 삭제
   * @param {String} processId
   */
  async deleteProcess(processId) {
    return await api('PROCESS_DELETE', {
      route: { processId },
    })
  },
  /**
   * 공정 종료
   * @param {String} processId
   */
  async closeProcess(processId) {
    return await api('PROCESS_CLOSE', {
      route: { processId },
    })
  },
  /**
   * 공정 생성
   * @param {RegisterNewProcess} form
   */
  async createProcess(form) {
    return await api('PROCESS_CREATE', {
      params: form,
    })
  },
  /**
   * 공정 편집
   * @param {EditProcessRequest} form
   */
  async editProcess(form) {
    return await api('PROCESS_UPDATE', {
      route: {
        processId: form.processId,
      },
      params: form,
    })
  },
}
