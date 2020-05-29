import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Task from '@/models/task/Task'
import SubTask from '@/models/task/SubTask'
import Step from '@/models/task/Step'

export default {
  /**
   * 작업 통계
   */
  async getTaskStatistics() {
    const data = await api('TASK_STATISTICS', {
      params: {
        workspaceUUID: store.getters['workspace/activeWorkspace'].uuid,
      },
    })
    return data
  },
  /**
   * 작업 일별 통계 조회
   * @param {String} month
   */
  async getTaskDailyRateAtMonth(month) {
    const data = await api('TASK_DAILY', {
      params: {
        month,
        workspaceUUID: store.getters['workspace/activeWorkspace'].uuid,
      },
    })
    return data.dailyTotal
  },
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
   * 작업 편집
   * @param {taskId} taskId
   */
  async updateTask(taskId, form) {
    form.actorUUID = store.getters['auth/myProfile'].uuid
    return await api('TASK_UPDATE', {
      route: { taskId },
      params: form,
    })
  },
  /**
   * 작업 종료
   * @param {taskId} taskId
   */
  async closeTask(taskId) {
    const actorUUID = store.getters['auth/myProfile'].uuid
    return await api('TASK_CLOSE', {
      route: { taskId },
      params: { taskId, actorUUID },
    })
  },
  /**
   * 작업 삭제
   * @param {taskId} taskId
   */
  async deleteTask(taskId) {
    const actorUUID = store.getters['auth/myProfile'].uuid
    return await api('TASK_DELETE', {
      params: { taskId, actorUUID },
    })
  },
  /**
   * 작업 타겟 정보 조회
   * @param {taskId} taskId
   */
  async getTargetInfo(taskId) {
    return await api('TARGET_INFO', {
      route: { taskId },
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
  /**
   * 하위 작업 상세조회
   * @param {String} subTaskId
   */
  async getSubTaskDetail(subTaskId) {
    const data = await api('SUB_TASK_INFO', {
      route: { subTaskId },
    })
    return new SubTask(data)
  },
  /**
   * 하위 작업 생성
   * @param {String} subTaskId
   * @param {form} form
   */
  async updateSubTask(subTaskId, form) {
    return await api('SUB_TASK_UPDATE', {
      route: { subTaskId },
      params: form,
    })
  },
  /**
   * 단계 검색
   * @param {String} subTaskId
   * @param {Object} params
   */
  async searchSteps(subTaskId, params = {}) {
    if (params.filter && params.filter[0] === 'ALL') {
      delete params.filter
    }
    const data = await api('STEPS_LIST', {
      route: { subTaskId },
      params: {
        size: 10,
        sort: 'updated_at,desc',
        ...params,
      },
    })
    return {
      list: data.steps.map(step => new Step(step)),
      total: data.pageMeta.totalElements,
    }
  },
}
