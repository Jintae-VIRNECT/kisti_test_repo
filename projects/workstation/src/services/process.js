import api from '@/api/gateway'
import { Process } from '@/models/process'

async function getProcessesList(params) {
  const data = await api('PROCESS_LIST', {
    params: {
      ...params,
      size: 10,
    },
  })
  return data.processes.map(process => Process(process))
}

export default {
  /**
   * 프로세스 통계
   */
  async getProcessesStatistics() {
    const data = await api('PROCESSES_STATISTICS')
    return data
  },
  /**
   * 프로세스 페이지 초기 리스트
   */
  async getDefaultProcessesList() {
    return await getProcessesList()
  },
  /**
   * 컨텐츠 검색
   */
  async searchProcesses(params) {
    return await getProcessesList(params)
  },
}
