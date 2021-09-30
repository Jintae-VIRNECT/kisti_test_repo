import http from 'api/gateway'

/**
 * 워크스페이스 메일 서버를 통해 메일 전송
 *
 * @query {Object} mailSendRequest {
    "receivers": [
      "example@virnect.com"
    ],
    "sender": "no-reply@virnect.com",
    "subject": "제목",
    "html": "string"
  }
 * @returns {Object} 성공여부
 */
export const sendEmail = async function({ receivers, sender, subject, html }) {
  const returnVal = await http('SEND_MAIL', {
    receivers,
    sender,
    subject,
    html,
  })
  return returnVal
}
