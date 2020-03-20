/**
 * 세부공정 구조
 * @param {Object} json
 */
export function SubProcess(json = {}) {
  return {
    id: json.subProcessId,
    name: json.name,
    priority: json.priority,
    jobTotal: json.jobTotal,
    conditions: json.conditions,
    startDate: json.startDate,
    endDate: json.endDate,
    progressRate: json.progressRate,
    reportedDate: json.reportedDate,
    isRecent: json.isRecent,
    workerUUID: json.workerUUID,
    issuesTotal: json.issuesTotal,
    doneCount: json.doneCount,
  }
}
