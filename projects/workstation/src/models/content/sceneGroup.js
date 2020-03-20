export function SceneGroup(json = {}) {
  return {
    id: json.id,
    priority: json.priority,
    name: json.name,
  }
}
