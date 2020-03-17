export function Content(json) {
  return {
    id: json.contentUUID,
    name: json.contentName,
    sceneTotal: json.sceneGroupTotal,
    uploaderName: json.uploaderName,
    status: json.status,
  }
}
