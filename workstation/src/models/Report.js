import Model from '@/models/Model'

export default class Report extends Model {
  /**
   * 보고 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
  }
}

/**
 * 보고 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [],
}
