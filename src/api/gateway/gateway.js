/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

// import axios from 'api/axios'
import { merge } from 'lodash'
// import Cookies from 'js-cookie'
import API from './api'
// import { logger, debug } from 'utils/logger'
import axios from '../axios'
import errorList from './gateway.error.json'
// import { cookieClear } from 'utils/auth'

const URL = API
// const TOKEN = Cookies.get('accessToken')

// logger('ENV', process.env.TARGET_ENV)

// axios.defaults.headers.common['Authorization'] = `Bearer ${TOKEN}`

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

    // Token
    // const accessToken = Cookies.get('accessToken')
    // axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`

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
        parameter.append(param, params[param])
      }
      // debug(option)
    } else {
      option.headers['Content-Type'] = 'application/json'
    }
  }

  option = merge(option, {
    ...custom,
  })
  option.headers = merge(option.headers, {
    ...headers,
  })

  // debug(method.toUpperCase(), url, parameter, headers)
  const request = {
    method: method,
    url: url,
    ...option,
  }
  if (method === 'get') {
    request['params'] = parameter
  } else {
    request['data'] = parameter
  }
  const response = await axios(request)
  return receiver(response)
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
        // debug(res.data['data'])
        return res.data['data']
      } else {
        return true
      }
    } else if (res.headers['content-type'] === 'application/octet-stream') {
      return res.data
    } else {
      throw errorHandler(res.data)
    }
  }
}

/**
 * Common error handler
 * @param {Object} errCode
 */
const errorHandler = function(err) {
  const error = {}
  error.code = isNaN(parseInt(err.code)) ? err : parseInt(err.code)
  error.message = err.message || 'Undefined Error.'

  console.error(`${error.message} (code: ${error.code})`)

  if (error.code in errorList) {
    // alert(error.message);
    switch (error.code) {
      case 9999:
        // console.error(error.message)
        // "Unexpected Server Error, Please contact Administrator"
        break
      case 8003:
      case 8005:
        // console.error(error.message)
        // cookieClear()
        window.location.reload()
        break
      // case 'Network Error':
      //   sessionStorage.clear()
      //   window.location.reload()
      //   break
    }
    return new Error(error.message)
  } else {
    return error
  }
}

export const setBaseURL = baseURL => {
  axios.defaults.baseURL = baseURL
  axios.defaults.headers['Access-Control-Allow-Origin'] = baseURL

  // debug('BASE_URL::', baseURL)
}

export default sender
