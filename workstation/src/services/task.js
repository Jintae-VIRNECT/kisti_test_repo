import { api } from '@/plugins/axios'
import Task from '@/models/process/Process'

export default {
  /**
   * 작업 검색
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
  /**
   * 작업 생성
   * @param {RegisterNewTask} form
   */
  async createTask(form) {
    return await api('PROCESS_CREATE', {
      params: form,
    })
  },
}
