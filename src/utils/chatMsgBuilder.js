import linkifyHtml from 'linkifyjs/html'

const typeList = ['me', 'opponent', 'system']
const subTypeList = ['alarm', 'people', 'cancel', 'ar', 'board']

export const normalMsgBuilder = (type, name, text) => {
  let msgObject = {}

  try {
    if (typeList.indexOf(type) < 0) {
      throw 'ChatMsgBuilder :: wrong type!! type :' + `${type}`
    }

    msgObject = {
      type: type,
      name: name,
      text: urlHyliter(text),
      date: new Date(),
    }
  } catch (e) {
    console.error(e)
  }

  return msgObject
}

export const fileMsgBuilder = (type, name, file) => {
  let msgObject = {}

  if (Array.isArray(file) && file.length > 0) {
    msgObject = {
      type: type,
      name: name,
      file: file,
      date: new Date(),
      url: '',
    }
  } else {
    console.error('ChatMsgBuilder :: argument "file" is invalid')
  }

  return msgObject
}

export const sysMsgBuilder = (subType, i18nText, name) => {
  let msgObject = {}

  const fileTransCancel = (name, i18nText) => {
    return (
      '<span class="emphasize">' +
      `${name}` +
      '</span>' +
      (i18nText ? i18nText : '님이 전송을 취소했습니다.')
    )
  }

  const peopleJoinAlarm = (name, i18nText) => {
    return (
      '<span class="emphasize">' +
      `${name}` +
      '</span>' +
      (i18nText ? i18nText : '님이 입장하셨습니다.')
    )
  }

  const arStart = i18nText => {
    return i18nText ? i18nText : 'AR 기능을 사용합니다.'
  }

  const boardStart = i18nText => {
    return i18nText ? i18nText : '협업 보드를 사용합니다.'
  }

  const alarmMessage = i18nText => {
    return i18nText
  }

  try {
    if (subTypeList.indexOf(subType) < 0) {
      throw 'ChatMsgBuilder :: wrong type!! subType :' + `${subType}`
    }

    msgObject = {
      text: '',
      name: name,
      date: new Date(),
      type: 'system',
      subType: subType,
    }

    if (subType === 'alarm') {
      msgObject.text = alarmMessage(i18nText)
    }

    if (subType === 'cancel') {
      msgObject.text = fileTransCancel(name, i18nText)
    }

    if (subType === 'ar') {
      msgObject.text = alarmMessage(i18nText)
    }

    if (subType === 'board') {
      msgObject.text = alarmMessage(i18nText)
    }

    if (subType === 'people') {
      msgObject.text = peopleJoinAlarm(name, i18nText)
    }

    msgObject.text = sysHyliter(msgObject.text)
  } catch (e) {
    console.error(e)
  }

  return msgObject
}

/**
 * check plain text to url linked msg
 * @param {string} chatText
 */
const urlHyliter = chatText => {
  let replaced = chatText

  try {
    replaced = linkifyHtml(chatText, {
      defaultProtocol: 'https',
      className: 'chat-url',
    })
  } catch (e) {
    console.log(e)
  }
  return replaced
}

const sysHyliter = chatText => {
  let replaced = chatText

  try {
    //check system type, and text length over 10
    if (chatText.indexOf('</span>') > 34) {
      replaced = chatText.replace('<span', '<p').replace('</span>', '</p>')
    }
  } catch (e) {
    console.log(e)
  }

  return replaced
}

export default {
  normalMsgBuilder: normalMsgBuilder,
  fileMsgBuilder: fileMsgBuilder,
  sysMsgBuilder: sysMsgBuilder,
}
