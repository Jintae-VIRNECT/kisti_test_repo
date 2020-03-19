/**
 * 콘텐츠 구조
 * @param {Object} json
 */
export function Content(json) {
  return {
    id: json.contentUUID,
    name: json.contentName,
    sceneTotal: json.sceneGroupTotal,
    uploaderName: json.uploaderName,
    status: json.status,
  }
}

/**
 * 콘텐츠 통계
 * @param {Object} json
 */
export function ContentStatistics(json) {
  return {
    totalContents: json.totalContents,
    totalManagedContents: json.totalManagedContents,
  }
}

/**
 * 콘텐츠 검색 필터설정
 */
export const filter = {
  value: ['ALL'],
  options: [
    {
      value: 'ALL',
      label: 'SearchTabNav.filter.all',
    },
    {
      value: 'MANAGED',
      label: 'SearchTabNav.filter.processPublished',
    },
    {
      value: 'WAIT',
      label: 'SearchTabNav.filter.processUnpublished',
    },
  ],
}

/**
 * 콘텐츠 검색 정렬설정
 */
export const sort = {
  value: 'createdDate,desc',
  options: [
    {
      value: 'createdDate,desc',
      label: 'SearchTabNav.sort.createdDesc',
    },
    {
      value: 'createdDate,asc',
      label: 'SearchTabNav.sort.createdAsc',
    },
  ],
}
