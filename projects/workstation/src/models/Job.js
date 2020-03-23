import Model from '@/models/Model'

export default class Job extends Model {
  /**
   * 작업 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.id = json.id
    this.name = json.name
    this.conditions = json.conditions
    this.progressRate = json.progressRate
    this.report = json.report
    this.issue = json.issue
    this.smartTool = json.smartTool
  }
}
