/**
 * 이슈 구조
 * @param {Object} json
 */
export function Issue(json) {
  return {
    id: json.issueId,
    caption: json.caption,
    worker: json.workerUUID,
    reportedDate: json.reportedDate,
  }
}
