import Model from '@/models/Model'

export default class SmartToolItem extends Model {
  /**
   * 스마트툴 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id
    this.batchCount = json.batchCount
    this.workingToque = json.workingToque
    this.result = json.result
  }
}
