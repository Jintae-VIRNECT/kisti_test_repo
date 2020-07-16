import linkifyHtml from 'linkifyjs/html'
import { TYPE, SUB_TYPE } from 'configs/chat.config'
import logger from 'utils/logger'
class ChatMsg {
  constructor() {}
}

class ChatMsgBuilder {
  constructor() {
    this.msg = new ChatMsg()
  }

  setName(name, useHylite) {
    if (useHylite) {
      this.msg.name = this.nameHyliter(name)
    } else {
      this.msg.name = name
    }

    return this
  }

  setType(type) {
    if (TYPE.hasOwnProperty(type)) {
      this.msg.type = type
    } else {
      throw 'ChatMsgBuilder :: wrong type!! type :' + `${type}`
    }
    return this
  }

  setSubType(subType) {
    if (SUB_TYPE.hasOwnProperty(subType)) {
      this.msg.subType = subType
    } else {
      throw 'ChatMsgBuilder :: wrong type!! subType :' + `${subType}`
    }
    return this
  }
  setText(text) {
    this.msg.text = text
    return this
  }

  setFile(file) {
    if (Array.isArray(file) && file.length > 0) {
      this.msg.file = file
    } else {
      throw 'ChatMsgBuilder :: argument "file" is invalid'
    }
    return this
  }

  urlHyliter(chatText) {
    try {
      if (chatText) {
        chatText = linkifyHtml(chatText, {
          defaultProtocol: 'https',
          className: 'chat-url',
        })
      } else {
        chatText = ''
      }
    } catch (e) {
      logger(e)
    }
    return chatText
  }

  nameHyliter(name) {
    return '<span class="emphasize">' + `${name}` + '</span>'
  }

  sysHyliter(chatText) {
    let replaced = chatText

    try {
      //check system type, and text length over 10
      if (chatText.indexOf('</span>') > 34) {
        replaced = chatText.replace('<span', '<p').replace('</span>', '</p>')
      }
    } catch (e) {
      logger(e)
    }

    return replaced
  }

  build() {
    if (this.msg.type === TYPE.SYSTEM && this.msg.subType !== undefined) {
      this.msg.text = this.sysHyliter(this.msg.text)
    }

    if (this.msg.type !== TYPE.SYSTEM) {
      this.msg.text = this.urlHyliter(this.msg.text)
    }

    //need hylite name
    if (
      this.msg.type === TYPE.SYSTEM &&
      (this.msg.subType === SUB_TYPE.PEOPLE ||
        this.msg.subType === SUB_TYPE.CANCEL)
    ) {
      this.msg.text = this.msg.name + ' ' + this.msg.text
    }

    if (this.msg.file !== undefined) {
      //if file property defined, remove text
      this.msg.text = undefined
    }

    this.msg.date = new Date()

    return this.msg
  }
}

export default ChatMsgBuilder
