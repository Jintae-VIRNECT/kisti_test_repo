/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

import Axios from 'axios'
import ErrorList from './gateway.error.json'
import { merge } from 'lodash'
// import { log } from 'utils/log'

const URL = {
  /* Account */
  LOGIN: ['POST', 'http://192.168.6.3:8321/auth/signin'],
  // ACCESS_TOKEN: ['POST', '/api/auth/accessToken'],

  /* CONFIGURATION */
  //!!!!!!Warning! API Request url is not fixed!!!!!!
  GET_CONFIG: ['GET', '/api/media/properties/'],
  UPDATE_CONFIG: ['POST', '/api/media/properties/'],
  PUT_CONFIG: ['PUT', '/api/media/properties/'],
}

const axios = Axios.create({
  timeout: 10000,
  withCredentials: false,
  headers: {
    // 'Access-Control-Allow-Origin': 'https://virnectremote.com',
    'Content-Type': 'application/json',
    client: 'web',
  },
})

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

  // return new Promise(function(resolve, reject) {
  //   // log(url, parameter, option.headers);
  //   log(url, parameter)

  //   axios[method](url, parameter, option)
  //     .then(function(res) {
  //       receiver(res, resolve)
  //     })
  //     .catch(function(err) {
  //       errorHandler(err, reject)
  //     })
  // })
}

/**
 * Common response handler
 * @param {Object} res
 */
const receiver = function(res) {
  if (res.data) {
    const code = res.data['code']
    if (code === 200) {
      const data = res.data['data']
      return data
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
