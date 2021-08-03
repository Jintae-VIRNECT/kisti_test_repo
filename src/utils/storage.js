// import SecureStorage from 'secure-web-storage'
// import CryptoJS from 'crypto-js'
// import { V, R } from './authsuger'

// const secKey = V + 'klSTk' + V + R
const originLocalStorage = window.localStorage

//스토리지 데이터 추출 파싱 함수
function parsingItem(value) {
  let getValue = value

  if ('undefined' == getValue) {
    return undefined
  } else if ('null' == getValue) {
    return null
  }

  try {
    getValue = JSON.parse(getValue)
  } catch (e) {
    if (getValue === 'true') {
      getValue = true
    } else if (getValue === 'false') {
      getValue = false
    } else if (getValue == parseInt(getValue)) {
      getValue = parseInt(getValue)
    }
  }

  return getValue
}

function stringifyItem(value) {
  return JSON.stringify(value)
}

//세션 스토리지...
// export const sessionStorage = new SecureStorage(window.sessionStorage, {
//   hash: key => {
//     key = CryptoJS.SHA256(key, atob(secKey + '=='))
//     return key.toString()
//   },
//   encrypt: data => {
//     data = CryptoJS.AES.encrypt(data, atob(secKey + '=='))
//     data = data.toString()
//     return data
//   },
//   decrypt: data => {
//     data = CryptoJS.AES.decrypt(data, atob(secKey + '=='))
//     data = data.toString(CryptoJS.enc.Utf8)
//     return data
//   },
// })

const setDevice = (d, i, v) => {
  let deviceInfo = parsingItem(originLocalStorage.getItem('deviceInfo'))
  if (!deviceInfo) {
    deviceInfo = {}
  }
  if (!deviceInfo[d]) {
    deviceInfo[d] = {}
  }
  deviceInfo[d][i] = v
  localStorage.setItem('deviceInfo', deviceInfo)
}

// const setRecord = (i, v) => {
//   let recordInfo = parsingItem(originLocalStorage.getItem('recordInfo'))
//   if (!recordInfo) {
//     recordInfo = {}
//   }
//   recordInfo[i] = v
//   localStorage.setItem('recordInfo', recordInfo)
// }

// const setServerRecord = (i, v) => {
//   let serverRecordInfo = parsingItem(
//     originLocalStorage.getItem('serverRecordInfo'),
//   )
//   if (!serverRecordInfo) {
//     serverRecordInfo = {}
//   }
//   serverRecordInfo[i] = v
//   localStorage.setItem('serverRecordInfo', serverRecordInfo)
// }

// const setAllow = (i, v) => {
//   let allow = parsingItem(originLocalStorage.getItem('allow'))
//   if (!allow) {
//     allow = {}
//   }
//   allow[i] = v
//   localStorage.setItem('allow', allow)
// }

// const setTranslate = (i, v) => {
//   let allow = parsingItem(originLocalStorage.getItem('translate'))
//   if (!allow) {
//     allow = {}
//   }
//   allow[i] = v
//   localStorage.setItem('translate', allow)
// }

// const setDrawingInfo = (i, v) => {
//   let drawingInfo = parsingItem(originLocalStorage.getItem('drawingInfo'))
//   if (!drawingInfo) {
//     drawingInfo = {}
//   }
//   drawingInfo[i] = v
//   localStorage.setItem('drawingInfo', drawingInfo)
// }

//로컬 스토리지
export const localStorage = {
  ...window.localStorage,
  getItem: k => parsingItem(originLocalStorage.getItem(k)),
  setItem: (k, v) => originLocalStorage.setItem(k, stringifyItem(v)),
  setDevice: (d, i, v) => setDevice(d, i, v),
  // setRecord: (i, v) => setRecord(i, v),
  // setServerRecord: (i, v) => setServerRecord(i, v),
  // setAllow: (i, v) => setAllow(i, v),
  // setTranslate: (i, v) => setTranslate(i, v),
  // setDrawingInfo: (i, v) => setDrawingInfo(i, v),
  removeItem: k => originLocalStorage.removeItem(k),
  clear: () => originLocalStorage.clear(),
  key: () => originLocalStorage.key(),
}

//사용자 별 로컬 스토리지
export class MyStorage {
  constructor(uuid) {
    this.uuid = uuid.slice(0, 16) //보안강화
    this.storage = {}
    this.key = Object.keys(this.storage)
    this.length = this.key.length
    this.init()
  }
  init() {
    const id = localStorage.getItem('account')
    if (this.uuid !== id) {
      originLocalStorage.removeItem(id)
    }
    if (originLocalStorage.getItem(this.uuid)) {
      this.storage = parsingItem(originLocalStorage.getItem(this.uuid))
    } else {
      originLocalStorage.setItem(this.uuid, stringifyItem({}))
    }
    localStorage.setItem('account', this.uuid)
  }
  getItem(keyname) {
    if (this.storage) {
      return this.storage[keyname]
    }
  }
  setItem(keyname, value) {
    this.storage[keyname] = value
    originLocalStorage.setItem(this.uuid, stringifyItem(this.storage))
  }
  setDevice(d, i, v) {
    let deviceInfo = parsingItem(this.storage['deviceInfo'])
    if (!deviceInfo) {
      deviceInfo = {}
    }
    if (!deviceInfo[d]) {
      deviceInfo[d] = {}
    }
    deviceInfo[d][i] = v
    this.storage['deviceInfo'] = deviceInfo
    originLocalStorage.setItem(this.uuid, stringifyItem(this.storage))
  }
  setItemPiece(k, i, v) {
    let info = parsingItem(this.storage[k])
    if (!info) {
      info = {}
    }
    info[i] = v
    this.storage[k] = info
    originLocalStorage.setItem(this.uuid, stringifyItem(this.storage))
  }
  removeItem(keyname) {
    delete this.storage[keyname]
    originLocalStorage.setItem(this.uuid, stringifyItem(this.storage))
  }
  clear() {
    if (this.storage) {
      originLocalStorage.removeItem(this.uuid)
    }
  }
}

// export default sessionStorage
