/**
 * 멤버 목록 정렬 함수
 *
 * MASTER, MANAGER를 먼저 앞에 오게 정렬한다.
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
  } else {
    return 0
  }
}
