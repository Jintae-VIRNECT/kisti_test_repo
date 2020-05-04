import Model from '@/models/Model'
import Report from '@/models/job/Report'
import Issue from '@/models/job/Issue'
import SmartTool from '@/models/job/SmartTool'

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
    this.report = json.report && new Report(json.report)
    this.issue = json.issue && new Issue(json.issue)
    this.smartTool = json.smartTool && new SmartTool(json.smartTool)
  }
}
