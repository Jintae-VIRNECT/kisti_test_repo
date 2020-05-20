import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Task from '@/models/task/Task'
import SubTask from '@/models/task/SubTask'

export default {
  /**
   * 작업 검색
   * @param {Object} params
   */
  async searchTasks(params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('TASK_LIST', {
      params: {
        workspaceUUID: store.getters['workspace/activeWorkspace'].uuid,
        size: 10,
        sort: 'updatedDate,desc',
        ...params,
      },
    })
    return {
      list: data.tasks.map(task => new Task(task)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 작업 상세조회
   * @param {String} taskId
   */
  async getTaskDetail(taskId) {
    const data = await api('TASK_INFO', {
      route: { taskId },
    })
    return new Task(data)
  },
  /**
   * 작업 생성
   * @param {RegisterNewTask} form
   */
  async createTask(form) {
    return await api('TASK_CREATE', {
      params: form,
    })
  },
  /**
   * 하위 작업 검색
   * @param {String} taskId
   * @param {Object} params
   */
  async searchSubTasks(taskId, params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('SUB_TASK_LIST', {
      route: { taskId },
      params: {
        workspaceUUID: store.getters['workspace/activeWorkspace'].uuid,
        size: 10,
        sort: 'updated_at,desc',
        ...params,
      },
    })
    return {
      list: data.subTasks.map(subTask => new SubTask(subTask)),
      total: data.pageMeta.totalElements,
    }
  },
}
