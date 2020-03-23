export default class Job {
  /**
   * 작업 구조
   * @param {Object} json
   */
  constructor(json) {
    this.id = json.id
    this.name = json.name
    this.conditions = json.conditions
    this.progressRate = json.progressRate
    this.report = json.report
    this.issue = json.issue
    this.smartTool = json.smartTool
  }
}
