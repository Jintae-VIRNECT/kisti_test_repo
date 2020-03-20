import api from '@/api/gateway'
import { SubProcess } from '@/models/process/subProcess'

export default {
  /**
   * 공정 하위 세부공정 리스트 검색
   * @param {String} processId
   * @param {Object} params
   */
  async searchChildSubProcesses(processId, params) {
    const data = await api('SUB_PROCESS_LIST', {
      route: { processId },
      params: { ...params },
    })
    return data.subProcesses.map(subProcess => SubProcess(subProcess))
  },
  /**
   * 세부공정 상세 정보
   * @param {String} subProcessId
   */
  async getSubProcessInfo(subProcessId) {
    const data = await api('SUB_PROCESS_INFO', {
      route: { subProcessId },
    })
    return SubProcess(data)
  },
}
