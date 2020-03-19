/**
 * 작업 구조
 * @param {Object} json
 */
export function Job(json) {
  return {
    id: json.id,
    name: json.name,
    conditions: json.conditions,
    progressRate: json.progressRate,
    report: json.report,
    issue: json.issue,
    smartTool: json.smartTool,
  }
}
