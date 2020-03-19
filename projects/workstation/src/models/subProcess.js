/**
 * 세부공정 구조
 * @param {Object} json
 */
export function SubProcess(json) {
  return {
    id: json.subProcessId,
    name: json.name,
    jobTotal: json.jobTotal,
    conditions: json.conditions,
    subProcessTotal: json.subProcessTotal,
    progressRate: json.progressRate,
    workerId: json.workerUUID,
  }
}
