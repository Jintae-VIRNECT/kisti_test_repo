import http from 'api/gateway'

/**
 * 진행중인 원격협업 리스트 조회
 */
export const getRoomListOld = async function() {
  const returnVal = await http('ROOM_LIST', { paging: false })

  return returnVal
}

/**
 * 원격협업 상세조회
 * @param {String} roomId
 */
export const getRoomInfoOld = async function({ roomId }) {
  const returnVal = await http('ROOM_INFO', {
    roomId,
  })

  return returnVal
}

/**
 * 원격협업 정보 변경
 * @param {String} title
 * @param {String} description
 * @param {String} image (File 객체로 변경될 수도 있음)
 */
export const updateRoomInfo = async function({ title, description, image }) {
  // const returnVal = await http('UPDATE_ROOM_INFO', {
  //   title,
  //   description,
  //   image,
  // })
  console.log('UPDATE_ROOM_INFO::param::', title, description, image)
  const returnVal = true

  return returnVal
}

/**
 * 원격협업 방 나가기
 * @param {String} roomId
 * @param {String} participantsId
 */
export const leaveRoom = async function({ roomId, participantsId }) {
  // const returnVal = await http('LEAVE_ROOM', {
  //   roomId,
  //   participantsId,
  // })
  console.log('LEAVE_ROOM')
  console.log('param::', roomId, participantsId)
  const returnVal = true

  return returnVal
}

/**
 * 원격협업 통화방 멤버 조회
 * @param {String} roomId
 */
export const participantsList = async function({ roomId }) {
  // const returnVal = await http('PARTICIPANTS_LIST', {
  //   roomId,
  // })
  // console.log('PARTICIPANTS_LIST')
  // console.log('param::', roomId)
  const returnVal = {}

  return returnVal
}

/**
 * 원격협업 통화방 멤버 추가 후보 리스트 조회
 */
export const inviteParticipantsList = async function() {
  const returnVal = await http('INVITE_PARTICIPANTS_LIST')

  return returnVal
}

/**
 * 원격협업 통화방 생성
 * @type  {FormData}
 * @param {File} file // 프로필 이미지 파일
 * @param {String} title
 * @param {String} description
 * @param {String} leaderId // 리더 아이디(uuid)
 * @param {Array[String]} participants // 참여자 리스트
 * @param {String} workspaceId
 */
export const createRoomOld = async function({
  file,
  title,
  description,
  leaderId,
  participants = [],
  workspaceId,
}) {
  const returnVal = await http('CREATE_ROOM', {
    file,
    title,
    description,
    leaderId,
    participants,
    workspaceId,
  })

  return returnVal
}

/**
 * 원격협업 통화방 삭제
 * @param {String} roomId
 */
export const deleteRoom = async function({ roomId }) {
  const returnVal = await http('DELETE_ROOM', {
    roomId,
  })

  return returnVal
}

/////////////////////////////////////////////////////////////////////

/**
 * TODO: file 입력 테스트
 * 원격협업 통화방 생성
 * @type  {FormData}
 * @param {File} file // 프로필 이미지 파일
 * @param {String} title
 * @param {String} description
 * @param {String} autoRecording
 * @param {String} leaderId // 리더 아이디(uuid)
 * @param {String} leaderEmail
 * @param {Array[String]} participants // 참여자 리스트
 * @param {String} workspaceId
 */
export const createRoom = async ({
  file,
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
export const getRoomInfo = async function({ sessionId, workspaceId }) {
  const returnVal = await http('ROOM_INFO', {
    sessionId,
    workspaceId,
  })

  return returnVal
}
