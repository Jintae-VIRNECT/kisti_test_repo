import api from '@/api/gateway'
import Task from '@/models/process/Process'

export default {
  /**
   * 공정 검색
   * @param {String} workspaceUUID
   * @param {Object} params
   */
  async searchTasks(workspaceUUID, params) {
    const data = await api('PROCESS_LIST', {
      params: {
        workspaceUUID,
        size: 10,
        sort: 'updated_at,desc',
        ...params,
      },
    })
    return {
      list: data.processes.map(process => new Task(process)),
      total: data.pageMeta.totalElements,
    }
  },
}
