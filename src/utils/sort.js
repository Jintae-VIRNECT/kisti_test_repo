/**
 * 멤버 목록 정렬 함수
 *
 *
 * 정렬 순
 * MASTER, MANAGER, MEMBER, GUEST
 * @param {*} A
 * @param {*} B
 * @returns
 */
export const memberSort = (A, B) => {
  if (A.role === 'MASTER') {
    return -1
  } else if (B.role === 'MASTER') {
    return 1
  } else if (A.role === 'MANAGER' && B.role !== 'MANAGER') {
    return -1
  } else if (A.role === 'GUEST' && B.role !== 'GUEST') {
    return 1
  } else if (B.role === 'GUEST' && A.role !== 'GUEST') {
    return -1
  } else {
    return 0
  }
}
