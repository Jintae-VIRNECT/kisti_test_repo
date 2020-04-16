import Model from '@/models/Model'

export default class ProcessStatistics extends Model {
  /**
   * 공정 통계
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.totalProcesses = json.totalProcesses
  }
}
