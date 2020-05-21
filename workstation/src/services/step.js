import { api } from '@/plugins/axios'
import Step from '@/models/step/Step'

export default {
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
