import http from 'api/gateway'
import { ROLE } from 'configs/remote.config'
/**
 * 멤버 리스트 호출
 * @param {String} filter 사용자 필터(MASTER, MANAGER, MEMBER) default ''
 * @param {Number} page 페이지 인덱스 넘버 default 0
 * @param {Number} size 페이지 데이터 사이즈 default 100
 * @param {String} sort 정렬 기준 default 'role, desc'
 * @param {String} workspaceId 조회할 워크스페이스 id
 * @param {Boolean} includeGuest Seat 멤버 포함 여부
 */
export const getMemberList = async function({
  filter = '',
  page = 0,
  size = 50,
  sort = 'email,desc',
  workspaceId,
  userId,
  includeGuest = true,
}) {
  const returnVal = await http('MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
    userId,
  })

  if (!includeGuest) {
    returnVal.memberList = returnVal.memberList.filter(member => {
      return member.role !== ROLE.GUEST
    })
  }

  return returnVal
}

/**
 * 원격협업 멤버 내보내기
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {String} participantId
 */
export const kickoutMember = async ({
  sessionId,
  workspaceId,
  leaderId,
  participantId,
}) => {
  const returnVal = await http('KICKOUT_MEMBER', {
    sessionId,
    workspaceId,
    leaderId,
    participantId,
  })

  return returnVal
}

/**
 * 초대 가능 멤버 리스트
 * @param {String} workspaceId
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} filter
 * @param {String} page
 * @param {String} size
 * @param {String} sort
 * @param {Boolean} includeGuest
 */
export const invitableList = async function({
  filter = '',
  page = 0,
  size = 50,
  sort = 'email,desc',
  workspaceId,
  sessionId,
  userId,
  includeGuest = false,
}) {
  const returnVal = await http('INVITABLE_MEMBER_LIST', {
    filter,
    page,
    size,
    sort,
    workspaceId,
    sessionId,
    userId,
  })

  if (!includeGuest) {
    returnVal.memberList = returnVal.memberList.filter(member => {
      return member.role !== ROLE.GUEST
    })
  }

  return returnVal
}

/**
 * 원격협업 초대
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} leaderId
 * @param {Array[String]} participantIds
 */
export const inviteRoom = async ({
  sessionId,
  workspaceId,
  leaderId,
  participantIds = [],
}) => {
  const returnVal = await http('INVITE_ROOM', {
    sessionId,
    workspaceId,
    leaderId,
    participantIds,
  })

  return returnVal
}

/**
 * 멤버 그룹 목록 반환
 *
 * @query {String} workspaceId
 * @parm {String} userId
 * @returns 멤버 그룹 목록
 */
export const getMemberGroupList = async ({ workspaceId, userId }) => {
  const returnVal = await http('PRIVATE_MEMBER_GROUP_LIST', {
    workspaceId,
    userId,
  })

  return returnVal
}

/**
 *
 * 단일 멤버 그룹 정보 반환
 *
 * @query {String} workspaceId
 * @query {String} groupId
 * @query {String} uuid
 * @returns 단일 멤버 그룹 정보
 */
export const getMemberGroupItem = async ({ workspaceId, groupId, userId }) => {
  const returnVal = await http('PRIVATE_MEMBER_GROUP_LIST_ITEM', {
    workspaceId,
    groupId,
    userId,
  })

  return returnVal
}

/**
 * 멤버 그룹 생성
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @param {String} groupName 그룹의 이름
 * @param {Array[String]} memberList 유저 uuid 목록
 * @returns 요청 응답
 */
// export const createMemberGroup = async ({
//   workspaceId,
//   userId,
//   groupName,
//   memberList,
// }) => {
//   const returnVal = await http('CREATE_MEMBER_GROUP', {
//     workspaceId,
//     userId,
//     groupName,
//     memberList,
//   })

//   return returnVal
// }

/**
 * 멤버 그룹 생성
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @param {String} groupName 그룹의 이름
 * @param {Array[String]} memberList 유저 uuid 목록
 * @returns 요청 응답
 */
export const createPrivateMemberGroup = async ({
  workspaceId,
  userId,
  groupName,
  memberList,
}) => {
  const returnVal = await http('CREATE_PRIVATE_MEMBER_GROUP', {
    workspaceId,
    userId,
    groupName,
    memberList,
  })

  return returnVal
}

/**
 * 멤버 그룹 삭제
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @query {String} groupId 그룹의 이름
 * @returns 요청 응답
 */
export const deletePrivateMemberGroup = async ({
  workspaceId,
  userId,
  groupId,
}) => {
  const returnVal = await http('DELETE_PRIVATE_MEMBER_GROUP', {
    workspaceId,
    userId,
    groupId,
  })

  return returnVal
}

/**
 * 멤버 그룹 업데이트
 *
 * @query {String} workspaceId
 * @query {String} userId
 * @query {String} groupId 그룹의 이름
 *
 * @returns 요청 응답
 */
export const updatePrivateMemberGroup = async ({
  workspaceId,
  userId,
  groupId,
  groupName,
  memberList,
}) => {
  const returnVal = await http('UPDATE_PRIVATE_MEMBER_GROUP', {
    workspaceId,
    userId,
    groupId,
    groupName,
    memberList,
  })

  return returnVal
}
