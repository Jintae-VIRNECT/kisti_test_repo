import http from 'api/gateway'

/**
 * 워크스페이스에 있는 하위 멤버 그룹 목록 조회
 *
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} userId 유저 id
 * @param {String} includeOneself 멤버에 본인 포함 여부 default true
 *
 *
 * @returns 워크스페이스 하위 멤버 그룹
 */
export const getSubGroups = async ({
  userId,
  workspaceId,
  includeOneself = true,
}) => {
  const returnVal = await http('SUB_GROUPS', {
    userId,
    workspaceId,
    includeOneself,
  })
  returnVal.groupInfoResponseList = returnVal.groupInfoResponseList.map(
    (subGroup, index) => {
      subGroup.index = index + 1
      return subGroup
    },
  )
  return returnVal
}

/**
 * 워크스페이스 하위 멤버 그룹 단일 항목 조회
 *
 * @param {String} groupId 하위 멤버 그룹 id
 * @param {String} userId 유저 id
 * @param {String} workspaceId 워크스페이스 id
 * @param {String} includeOneself 멤버에 본인 포함 여부 default true
 *
 *
 * @returns 워크스페이스 하위 멤버 그룹 단일 항목 정보
 */
export const getSubGroupItem = async ({
  groupId,
  userId,
  workspaceId,
  includeOneself = true,
}) => {
  const returnVal = await http('SUB_GROUP_ITEM', {
    groupId,
    userId,
    workspaceId,
    includeOneself,
  })
  return returnVal
}

/**
 * 워크스페이스 하위 멤버 그룹 생성
 *
 * @param {String} groupName 하위 멤버 그룹 이름
 * @param {String[Object]} memberList 하위 멤버 그룹 멤버 리스트
 * @param {String} userId 유저 id
 * @param {String} workspaceId 워크스페이스 id
 *
 * @returns 생성된 워크스페이스 하위 멤버 그룹 정보
 */

export const createSubGroup = async ({
  groupName,
  memberList,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('CREATE_SUB_GROUP', {
    groupName,
    memberList,
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 워크스페이스 하위 멤버 그룹 업데이트
 *
 * @param {String} groupId 하위 멤버 그룹 id
 * @param {String} groupName 하위 멤버 그룹 name
 * @param {String[Object]} memberList 하위 멤버 그룹 멤버 리스트
 * @param {String} userId 유저 id
 * @param {String} workspaceId 워크스페이스 id
 *
 * @returns 생성된 워크스페이스 하위 멤버 그룹 정보
 */

export const updateSubGroup = async ({
  groupId,
  groupName,
  memberList,
  userId,
  workspaceId,
}) => {
  const returnVal = await http('UPDATE_SUB_GROUP', {
    groupId,
    groupName,
    memberList,
    userId,
    workspaceId,
  })
  return returnVal
}

/**
 * 워크스페이스 하위 멤버 그룹 삭제
 *
 * @param {String} groupId 하위 멤버 그룹 id
 * @param {String} userId 유저 id
 * @param {String} workspaceId 워크스페이스 id
 *
 * @returns 생성된 워크스페이스 하위 멤버 그룹 정보
 */

export const deleteSubGroup = async ({ groupId, userId, workspaceId }) => {
  const returnVal = await http('DELETE_SUB_GROUP', {
    groupId,
    userId,
    workspaceId,
  })
  return returnVal
}
