/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

import axios from 'api/axios'
import ErrorList from './gateway.error.json'
import { merge } from 'lodash'
// import { log } from 'utils/log'

const URL = {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  // ACCESS_TOKEN: ['POST', '/api/auth/accessToken'],

  /* CONFIGURATION */
  //!!!!!!Warning! API Request url is not fixed!!!!!!
  GET_CONFIG: ['GET', '/api/media/properties/'],
  UPDATE_CONFIG: ['POST', '/api/media/properties/'],
  PUT_CONFIG: ['PUT', '/api/media/properties/'],

  /* Workspace - History */
  GET_HISTORY_LIST: ['GET', '/api/media/history'],
  GET_HISTORY_ITEM: ['GET', '/api/media/history/{roomId}'],
  DELETE_HISTORY_ITEM: ['DELETE', '/api/media/history/{roomId}'],
  DELETE_HISTORY_ALL: ['DELETE', '/api/media/history/'],

  /* Workspace - Member */
  GET_MEMBER_LIST: ['GET', '/api/media/member/'],

  /* Workspace - Room */
  ROOM_LIST: ['GET', '/api/media/room'],
  ROOM_INFO: ['GET', '/api/media/room/{roomId}'],
  UPDATE_ROOM_INFO: ['PUT', '/api/media/room/{roomId}', { type: 'form' }],
  LEAVE_ROOM: [
    'DELETE',
    '/api/media/room/{roomId}/participants/{participantsId}',
  ],
  PARTICIPANTS_LIST: ['GET', '/api/media/room/{roomId}/participants'],
  INVITE_PARTICIPANTS_LIST: ['GET', '/api/media/room/participants'],
  CREATE_ROOM: ['POST', ' /api/media/room', { type: 'form' }],
  DELETE_ROOM: ['DELETE', '/api/media/room/{roomId}'],
}

console.log(`ENV: ${process.env.TARGET_ENV}`)

/**
 * Common request handler
 * @param {String} constant
 * @param {Object} params
 * @param {Object} custom
 */
// const timeout = ms => new Promise(resolve => setTimeout(resolve, ms))

const sender = async function(constant, params, custom) {
  constant = constant.toUpperCase()

  // let count = counter || 0

  // if (!axios.defaults.hasOwnProperty('baseURL')) {
  //   if (count < 10) {
  //     await timeout(0)
  //     return await sender(constant, params, custom, ++count)
  //   } else {
  //     return Promise.reject(new Error('BaseURL Undefined'))
  //   }
  // }
  let option = axios.defaults
  let url = URL[constant]
  let method = 'post'
  let parameter = params

  // URL 배열 타입 처리
  if (url instanceof Array) {
    //Extract method
    method = URL[constant][0].toLowerCase() || 'post'

    //Extract url
    url = URL[constant][1]

    //Extract option
    custom = URL[constant][2]

    if (custom && 'form' === custom.type) {
      option.headers['Content-Type'] = 'multipart/form-data'

      //Extract params
      parameter = new FormData()
      for (let param in params) {
        parameter.append(param, params[param])
      }
    } else {
      option.headers['Content-Type'] = 'application/json'

      //Extract params
      if ('get' !== method) {
        // parameter = JSON.stringify(parameter)
      } else {
        parameter = {
          params: parameter,
        }
      }
    }
  }

  option = merge(option, {
    ...custom,
  })

  // 정의되지 않은 URL 처리
  if (url === undefined) {
    throw new Error('Unknown API')
  }

  // URI 전환
  url = url.replace(/{(\w+)}/g, (match, $1) => {
    const alt = params[$1]
    delete params[$1]
    delete parameter[$1]
    return alt
  })
  try {
    console.log(method, url, parameter)
    const response = await axios[method](url, parameter, option)
    return receiver(response)
  } catch (error) {
    throw error
  }
}

/**
 * Common response handler
 * @param {Object} res
 */
const receiver = function(res) {
  if (res.data) {
    const code = res.data['code']
    if (code === 200) {
      if ('data' in res.data) {
        return res.data['data']
      } else {
        return true
      }
    } else {
      errorHandler(code)
    }
  }
}

/**
 * Common error handler
 * @param {Object} errCode
 */
const errorHandler = function(err) {
  const errorList = ErrorList
  const error = {}
  error.code = isNaN(parseInt(err)) ? err : parseInt(err)
  error.message = errorList[error.code] || 'Undefined Error.'

  if (error.code in errorList) {
    // alert(error.message);
    switch (error.code) {
      case 8005:
      case 8003:
        // sessionStorage.clear();
        // window.location.href = "/"
        sessionStorage.clear()
        window.location.reload()
        break
      case 'Network Error':
        sessionStorage.clear()
        window.location.reload()
        break
    }
    throw new Error(error.message)
  } else {
    throw err
    // window.sessionStorage.clear()
    // window.location.href = "/"
  }
}

export default sender
