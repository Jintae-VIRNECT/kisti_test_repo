import http from 'api/gateway'

/**
 * 멤버를 불러오는 서비스
 * @param {String} filter 사용자 필터(MASTER, MANAGER, MEMBER) default ''
 * @param {Number} page 페이지 인덱스 넘버 default 0
 * @param {Number} size 페이지 데이터 사이즈 default 100
 * @param {String} sort 정렬 기준 default 'role, desc'
 * @param {String} workspaceId 조회할 워크스페이스 id
 */
export const getMemberList = async function({
  filter = '',
  page = 0,
  size = 100,
  sort = 'role,desc',
  workspaceId,
}) {
  const returnVal = await http('MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
  })
  return returnVal
}
