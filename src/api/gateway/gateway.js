/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

// import axios from 'api/axios'
import { merge } from 'lodash'
import Cookies from 'js-cookie'
import API from './api'
import { logger } from 'utils/logger'
import axios from '../axios'

const URL = API
const TOKEN = Cookies.get('accessToken')

console.log(`ENV: ${process.env.TARGET_ENV}`)

axios.defaults.headers.common['Authorization'] = `Bearer ${TOKEN}`

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
        parameter.append(param, params[param])
      }
      logger(option)
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
    logger(method.toUpperCase(), url, parameter, headers)
    const request = {
      method: method,
      url: url,
      ...option,
    }
    if (method === 'get') {
      request['params'] = parameter.params
    } else {
      request['data'] = parameter
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
        logger(res.data['data'])
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
  logger('TOKEN::', axios.defaults.headers)
}

export const setBaseURL = baseURL => {
  axios.defaults.baseURL = baseURL
  logger('BASE_URL::', baseURL)
}

export default sender
