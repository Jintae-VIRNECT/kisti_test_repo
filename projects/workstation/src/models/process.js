export function Process(json) {
  return {
    id: json.id,
    name: json.name,
    position: json.position,
    conditions: json.conditions,
    subProcessTotal: json.subProcessTotal,
    doneCount: json.doneCount,
    issuesTotal: json.issuesTotal,
  }
}
