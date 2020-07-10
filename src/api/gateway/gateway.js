/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

// import axios from 'api/axios'
import ErrorList from './gateway.error.json'
import { merge } from 'lodash'
import Axios from 'axios'
import Cookies from 'js-cookie'
import urls from '@/server/urls'

const TOKEN = Cookies.get('accessToken')
const axios = Axios.create({
  timeout: 10000,
  withCredentials: false,
  headers: {
    'Access-Control-Allow-Origin': urls.api[process.env.TARGET_ENV],
    'Content-Type': 'application/json',
    common: {
      Authorization: `Bearer ${TOKEN}`,
    },
  },
  // baseURL: urls.api[process.env.TARGET_ENV],
  baseURL: 'https://192.168.6.4:4443',
})

const URL = {
  /* Account */
  LOGIN: ['POST', 'https://192.168.6.3:8073/auth/signin'],
  TOKEN: ['POST', 'https://192.168.6.3:8073/auth/oauth/token'],
  ACCOUNT: ['GET', 'https://192.168.6.3:8073/users/info', { type: 'form' }],
  // ACCESS_TOKEN: ['POST', '/auth/accessToken'],

  /* CONFIGURATION */
  //!!!!!!Warning! API Request url is not fixed!!!!!!
  GET_CONFIG: ['GET', '/media/properties/'],
  UPDATE_CONFIG: ['POST', '/media/properties/'],
  PUT_CONFIG: ['PUT', '/media/properties/'],

  /* Workspace - History */
  GET_HISTORY_LIST: ['GET', '/media/history'],
  GET_HISTORY_ITEM: ['GET', '/media/history/{roomId}'],
  DELETE_HISTORY_ITEM: ['PUT', '/media/history/{roomId}'],
  DELETE_HISTORY_ALL: ['DELETE', '/media/history'],

  /* Workspace - Member */
  GET_MEMBER_LIST: [
    'GET',
    'https://192.168.6.3:8073/workspaces/{workspaceId}/members?size={size}',
  ],
  // GET_MEMBER_LIST: ['GET', '/media/member/'],

  /* Workspace - Room */
  ROOM_LIST: ['GET', '/media/room?paging={paging}'],
  ROOM_INFO: ['GET', '/media/room/{roomId}'],
  UPDATE_ROOM_INFO: ['PUT', '/media/room/{roomId}', { type: 'form' }],
  LEAVE_ROOM: ['DELETE', '/media/room/{roomId}/participants/{participantsId}'],
  PARTICIPANTS_LIST: ['GET', '/media/room/{roomId}/participants'],
  INVITE_PARTICIPANTS_LIST: ['GET', '/media/room/participants'],
  CREATE_ROOM: [
    'POST',
    '/media/room?workspaceId={workspaceId}',
    { type: 'form' },
  ],
  DELETE_ROOM: ['DELETE', '/media/room/{roomId}'],

  /* CALL */
  GET_TOKEN: ['POST', '/media/tokens'],

  /* LICENSE */
  GET_LICENSE: [
    'GET',
    'https://192.168.6.3:8073/licenses/{workspaceId}/{userId}',
  ],

  /* CHAT FILE */
  SEND_FILE: [
    'POST',
    'https://192.168.13.94:4443/file/upload',
    { type: 'form' },
  ],
}

console.log(`ENV: ${process.env.TARGET_ENV}`)

/**
 * Common request handler
 * @param {String} constant
 * @param {Object} params
 * @param {Object} custom
 */
// const timeout = ms => new Promise(resolve => setTimeout(resolve, ms))

const sender = async function(constant, params, headers = {}, custom) {
  constant = constant.toUpperCase()

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

    //Extract option
    custom = URL[constant][2]

    if (custom && 'form' === custom.type) {
      option.headers['Content-Type'] = 'multipart/form-data'

      //Extract params
      let paramsOption = parameter
      parameter = new FormData()
      for (let param in paramsOption) {
        console.log('param ::', param)
        parameter.append(param, params[param])
      }
      console.log(option)
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
  option.headers = merge(option.headers, {
    ...headers,
  })

  try {
    console.log(method.toUpperCase(), url, parameter, headers)
    const request = {
      method: method,
      data: parameter,
      url: url,
      ...option,
    }
    const response = await axios(request)
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
  // console.log(res)
  if (res.data) {
    const code = res.data['code']
    if (code === 200) {
      if ('data' in res.data) {
        console.log(res.data['data'])
        return res.data['data']
      } else {
        return true
      }
    } else {
      errorHandler(res.data)
    }
  }
}

/**
 * Common error handler
 * @param {Object} errCode
 */
const errorHandler = function(err) {
  console.error(err)
  const errorList = ErrorList
  const error = {}
  error.code = isNaN(parseInt(err)) ? err : parseInt(err)
  error.message = errorList[error.code] || 'Undefined Error.'

  if (error.code in errorList) {
    // alert(error.message);
    switch (error.code) {
      case 9999:
        // "Unexpected Server Error, Please contact Administrator"
        break
      // case 8005:
      case 8003:
        // 토근만료, 갱신
        break
      // case 'Network Error':
      //   sessionStorage.clear()
      //   window.location.reload()
      //   break
    }
    // throw new Error(error.message)
  } else {
    throw err
    // window.sessionStorage.clear()
    // window.location.href = "/"
  }
}

export const setAuthorization = accessToken => {
  axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`
  console.log(axios.defaults.headers)
}

export default sender
