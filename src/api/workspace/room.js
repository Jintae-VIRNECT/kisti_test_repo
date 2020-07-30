import http from 'api/gateway'

/**
 * TODO: profile 입력 테스트
 * 원격협업 통화방 생성
 * @type  {FormData}
 * @param {File} profile // 프로필 이미지 파일
 * @param {String} title
 * @param {String} description
 * @param {String} autoRecording
 * @param {String} leaderId // 리더 아이디(uuid)
 * @param {String} leaderEmail
 * @param {Array[String]} participants // 참여자 리스트
 * @param {String} workspaceId
 */
export const createRoom = async ({
  profile,
  title,
  description,
  autoRecording = false,
  leaderId,
  leaderEmail,
  participants = [],
  workspaceId,
}) => {
  const returnVal = await http(
    'CREATE_ROOM',
    {
      title,
      description,
      autoRecording,
      leaderId,
      leaderEmail,
      participants,
      workspaceId,
    },
    // {},
    // {
    //   params: {
    //     file: file,
    //   },
    // },
  )

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
  search,
  size = 10,
  sort = 'createdDate,desc',
  userId,
  workspaceId,
}) => {
  const returnVal = await http('ROOM_LIST', {
    page,
    paging,
    search,
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
 * @param {String} email
 * @param {String} memberType
 * @param {String} deviceType
 * @query {String} sessionId
 * @query {String} workspaceId
 */
export const joinRoom = async ({
  uuid,
  email,
  memberType,
  deviceType,
  sessionId,
  workspaceId,
}) => {
  const returnVal = await http('JOIN_ROOM', {
    uuid,
    email,
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
 * TODO: profile 입력 테스트
 * @query {String} sessionId
 * @query {String} workspaceId
 * @param {File} profile
 * @param {String} title
 * @param {String} description
 */
export const updateRoomInfo = async ({
  profile,
  title,
  description,
  sessionId,
  workspaceId,
}) => {
  const returnVal = await http('UPDATE_ROOM_INFO', {
    // profile,
    title,
    description,
    sessionId,
    workspaceId,
  })

  return returnVal
}
