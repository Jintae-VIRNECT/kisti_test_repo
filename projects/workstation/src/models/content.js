export function Content(json) {
  return {
    id: json.contentUUID,
    name: json.contentName,
    sceneTotal: json.sceneGroupTotal,
    uploaderName: json.uploaderName,
    status: json.status,
  }
}

export const filter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'ALL',
    },
    {
      value: 'MANAGED',
      label: 'PROCESS_PUBLISHED',
    },
    {
      value: 'WAIT',
      label: 'PROCESS_NON_PUBLISHED',
    },
  ],
}

export const sort = {
  value: 'createdDate,desc',
  options: [
    {
      value: 'createdDate,desc',
      label: 'SORT_CREATED',
    },
    {
      value: 'createdDate,asc',
      label: 'SORT_CREATED_DESC',
    },
  ],
}
