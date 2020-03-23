export default class ProcessStatistics {
  /**
   * 공정 통계
   * @param {Object} json
   */
  constructor(json) {
    this.totalProcesses = json.totalProcesses
  }
}
