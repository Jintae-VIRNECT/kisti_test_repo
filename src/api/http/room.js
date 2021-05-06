import http from 'api/gateway'
import { ROOM_STATUS } from 'configs/status.config'

/**
 * 원격협업 통화방 생성
 * @header {String} client
 * @path {String} userId
 * @query {Number} companyCode
 * @param {String} title
 * @param {String} description
 * @param {String} autoRecording
 * @param {Boolean} translation // 번역 활성화 여부..?
 * @param {Boolean} keepAlive // 룸 세션 유지 여부
 * @param {String} sessionType // 원격 협업('PRIVATE') / 오픈방('PUBLIC')
 * @param {String} leaderId // 리더 아이디(uuid)
 * @param {Array[String]} participantIds // 참여자 리스트
 * @param {String} workspaceId
 */
export const createRoom = async ({
  client,
  userId,
  companyCode = 0,
  title,
  description,
  autoRecording = false,
  translation = false,
  keepAlive = false,
  sessionType = ROOM_STATUS.PRIVATE,
  leaderId,
  participantIds = [],
  workspaceId,
  videoRestrictedMode,
  audioRestrictedMode,
}) => {
  const returnVal = await http(
    'CREATE_ROOM',
    {
      userId,
      companyCode,
      title,
      description,
      autoRecording,
      translation,
      keepAlive,
      sessionType,
      leaderId,
      participantIds,
      workspaceId,
      videoRestrictedMode,
      audioRestrictedMode,
    },
    {
      client,
    },
  )

  return returnVal
}
/**
 * 원격협업 통화방 재시작
 * @header {String} client
 * @path {String} userId
 * @query {Number} companyCode
 * @query {String} sessionId
 * @param {String} title
 * @param {String} description
 * @param {String} autoRecording
 * @param {Boolean} translation // 번역 활성화 여부..?
 * @param {Boolean} keepAlive // 룸 세션 유지 여부
 * @param {String} sessionType // 원격 협업('PRIVATE') / 오픈방('PUBLIC')
 * @param {String} leaderId // 리더 아이디(uuid)
 * @param {Array[String]} participantIds // 참여자 리스트
 * @param {String} workspaceId
 */
export const restartRoom = async ({
  client,
  userId,
  companyCode = 0,
  sessionId,
  title,
  description,
  autoRecording = false,
  translation = false,
  keepAlive = false,
  sessionType = ROOM_STATUS.PRIVATE,
  leaderId,
  participantIds = [],
  workspaceId,
  videoRestrictedMode,
  audioRestrictedMode,
}) => {
  const returnVal = await http(
    'RESTART_ROOM',
    {
      userId,
      companyCode,
      sessionId,
      title,
      description,
      autoRecording,
      translation,
      keepAlive,
      sessionType,
      leaderId,
      participantIds,
      workspaceId,
      videoRestrictedMode,
      audioRestrictedMode,
    },
    {
      client,
    },
  )

  return returnVal
}
/**
 * 프로필 입력
 * @type {FormData}
 * @param {File} profile
 * @param {String} sessionId
 * @param {String} uuid
 * @param {String} workspaceId
 */
export const updateRoomProfile = async ({
  profile,
  sessionId,
  uuid,
  workspaceId,
}) => {
  const returnVal = await http('UPDATE_ROOM_PROFILE', {
    profile,
    sessionId,
    uuid,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 목록 조회
 * @param {String} userId
 * @query {String} workspaceId
 */
export const getRoomList = async ({
  page = 0,
  paging = false,
  size = 10,
  sort = 'createdDate,desc',
  userId,
  workspaceId,
}) => {
  const returnVal = await http('ROOM_LIST', {
    page,
    paging,
    size,
    sort,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 목록 검색
 * @param {String} userId
 * @query {String} workspaceId
 */
export const searchRoomList = async ({
  search = '',
  page = 0,
  paging = false,
  size = 10,
  sort = 'createdDate,asc',
  userId,
  workspaceId,
}) => {
  const returnVal = await http('ROOM_SEARCH', {
    search,
    page,
    paging,
    size,
    sort,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 참가
 * @param {String} uuid
 * @param {String} memberType
 * @param {String} deviceType
 * @query {String} sessionId
 * @query {String} workspaceId
 */
export const joinRoom = async ({
  uuid,
  memberType,
  deviceType,
  sessionId,
  workspaceId,
}) => {
  const returnVal = await http('JOIN_ROOM', {
    uuid,
    memberType,
    deviceType,
    sessionId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 상세조회
 * @param {String} sessionId
 * @param {String} workspaceId
 */
export const getRoomInfo = async ({ sessionId, workspaceId }) => {
  const returnVal = await http('ROOM_INFO', {
    sessionId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 삭제
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} workspaceId
 */
export const deleteRoom = async ({ sessionId, userId, workspaceId }) => {
  const returnVal = await http('DELETE_ROOM', {
    sessionId,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 나가기
 * @param {String} sessionId
 * @param {String} userId
 * @param {String} workspaceId
 */
export const leaveRoom = async ({ sessionId, userId, workspaceId }) => {
  const returnVal = await http('LEAVE_ROOM', {
    sessionId,
    userId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 정보 변경
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {String} uuid
 * @param {String} title
 * @param {String} description
 */
export const updateRoomInfo = async ({
  uuid,
  title,
  description,
  sessionId,
  workspaceId,
}) => {
  const returnVal = await http('UPDATE_ROOM_INFO', {
    uuid,
    title,
    description,
    sessionId,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 프로필 삭제
 * @query {String} sessionId
 * @query {String} workspaceId
 */
export const removeRoomProfile = async ({ sessionId, workspaceId }) => {
  const returnVal = await http('REMOVE_ROOM_PROFILE', {
    sessionId,
    workspaceId,
  })

  return returnVal
}
