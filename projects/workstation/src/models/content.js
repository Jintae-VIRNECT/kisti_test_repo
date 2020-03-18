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
      label: 'SearchTabNav.all',
    },
    {
      value: 'MANAGED',
      label: 'SearchTabNav.processPublished',
    },
    {
      value: 'WAIT',
      label: 'SearchTabNav.processUnpublished',
    },
  ],
}

export const sort = {
  value: 'createdDate,desc',
  options: [
    {
      value: 'createdDate,desc',
      label: 'SearchTabNav.sortCreatedDesc',
    },
    {
      value: 'createdDate,asc',
      label: 'SearchTabNav.sortCreatedAsc',
    },
  ],
}
