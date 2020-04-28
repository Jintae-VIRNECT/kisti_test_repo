// import http from 'api/gateway'
import roomlist from './roomlist.json'
import roomdetail from './roomdetail.json'
import participants from './participantslist.json'
import inviteParticipants from './inviteparticipantslist.json'

/**
 * 진행중인 원격협업 리스트 조회
 * @param {String} title
 * @param {String} participantName
 */
export const getRoomList = async function({ title, participantName }) {
  // const returnVal = await http('ROOM_LIST', {
  //   title,
  //   participantName,
  // })
  console.log('ROOM_LIST')
  console.log('param::', title, participantName)
  const returnVal = roomlist

  return returnVal
}

/**
 * 원격협업 상세조회
 * @param {String} roomId
 */
export const getRoomInfo = async function({ roomId }) {
  // const returnVal = await http('ROOM_INFO', {
  //   roomId,
  // })
  console.log('ROOM_INFO')
  console.log('param::', roomId)
  const returnVal = roomdetail

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
  console.log('UPDATE_ROOM_INFO')
  console.log('param::', title, description, image)
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
  console.log('PARTICIPANTS_LIST')
  console.log('param::', roomId)
  const returnVal = participants

  return returnVal
}

/**
 * 원격협업 통화방 멤버 추가 후보 리스트 조회
 */
export const inviteParticipantsList = async function() {
  // const returnVal = await http('INVITE_PARTICIPANTS_LIST')
  console.log('INVITE_PARTICIPANTS_LIST')
  const returnVal = inviteParticipants

  return returnVal
}

/**
 * 원격협업 통화방 생성
 * @param {String} title
 * @param {String} description
 * @param {String} sessionId
 * >> profile 변경될 예정
 * @param {Object} profile
 *  @param {String} path
 * @param {Array} roomParticipants
 *  @param {Object} participant
 *    @param {String} participantId
 *    @param {String} sessionId
 */
export const createRoom = async function({
  title,
  description,
  sessionId,
  profile,
  roomParticipants,
}) {
  // const returnVal = await http('CREATE_ROOM', {
  //   title,
  //   description,
  //   sessionId,
  //   profile,
  //   roomParticipants,
  // })
  console.log('CREATE_ROOM')
  console.log('param::', {
    title,
    description,
    sessionId,
    profile,
    roomParticipants,
  })
  const returnVal = '방 ID'

  return returnVal
}

/**
 * 원격협업 통화방 삭제
 * @param {String} title
 * @param {String} description
 * @param {String} sessionId
 * >> profile 변경될 예정
 * @param {Object} profile
 *  @param {String} path
 * @param {Array} roomParticipants
 *  @param {Object} participant
 *    @param {String} participantId
 *    @param {String} sessionId
 */
export const deleteRoom = async function({ roomId }) {
  // const returnVal = await http('DELETE_ROOM', {
  //   roomId,
  // })
  console.log('DELETE_ROOM')
  console.log('param::', { roomId })
  const returnVal = true

  return returnVal
}
