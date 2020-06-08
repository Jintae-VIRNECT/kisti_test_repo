import linkifyHtml from 'linkifyjs/html'

/**
 * @author ykmo-VIRNECT
 */

const typeList = ['me', 'opponent', 'system']
const subTypeList = ['alarm', 'people', 'cancel', 'ar', 'board']

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
    if (typeList.indexOf(type) < 0) {
      throw 'ChatMsgBuilder :: wrong type!! type :' + `${type}`
    } else {
      this.msg.type = type
    }
    return this
  }

  setSubType(subType) {
    if (subTypeList.indexOf(subType) >= 0) {
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
      console.log(e)
    }

    return replaced
  }

  build() {
    if (this.msg.type === 'system' && this.msg.subType !== undefined) {
      this.msg.text = this.sysHyliter(this.msg.text)
    }

    if (this.msg.type !== 'system') {
      this.msg.text = this.urlHyliter(this.msg.text)
    }

    //need hylite name
    if (
      this.msg.type === 'system' &&
      (this.msg.subType === 'people' || this.msg.subType === 'cancel')
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
