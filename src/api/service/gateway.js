/**
 * [FLOW]
 * sender() -> Axios -> receiver()/errorHandler()
 */

import Axios from 'axios'
import sessionStorage from 'utils/storage'
import ErrorList from './gateway.error.json'
import { merge } from 'lodash'
import { log } from 'utils/log'

const URL = {
  /* Account */
  // ACCESS_TOKEN: ['POST', '/api/auth/accessToken'],
}

const account = sessionStorage.getItem('account')

let TOKEN = ''
if (account && account.accessToken) {
  TOKEN = account.accessToken
}

const NODE_ENV = process.env.NODE_ENV
const isProduction = NODE_ENV === 'production' ? true : false

log('process env: ' + NODE_ENV + ' / is production: ' + isProduction)

const axios = Axios.create({
  timeout: 10000,
  withCredentials: isProduction,
  headers: {
    'Access-Control-Allow-Origin': 'https://virnectremote.com',
    'Content-Type': 'application/json',
    client: 'web',
  },
})

;(async () => {
  const res = await axios.post('/urls')
  axios.defaults.baseURL = res.data.GATEWAY_SERVICE_URL
})()

/**
 * Common request handler
 * @param {String} constant
 * @param {Object} params
 * @param {Object} custom
 */
const timeout = ms => new Promise(resolve => setTimeout(resolve, ms))

const sender = async function(constant, params, custom, counter) {
  constant = constant.toUpperCase()

  let count = counter || 0

  if (!axios.defaults.hasOwnProperty('baseURL')) {
    if (count < 10) {
      await timeout(0)
      return await sender(constant, params, custom, ++count)
    } else {
      return Promise.reject(new Error('BaseURL Undefined'))
    }
  }
  let option = axios.defaults
  let url = URL[constant]
  let method = 'post'
  let parameter = params

  //URL 배열 타입 처리(지금은 다 이거 쓰고있'돼지')
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

  //Set token
  // if(TOKEN) {
  // 	TOKEN = sessionStorage.getItem('account').accessToken
  //     option.headers.authorization = `Bearer ${TOKEN}`;
  // } else {
  const sessionAccount = sessionStorage.getItem('account')

  if (sessionAccount && sessionAccount.accessToken) {
    TOKEN = sessionAccount.accessToken
    option.headers.authorization = `Bearer ${TOKEN}`
  }
  // }

  //정의'돼지' 않은 URL 처리
  if (url === undefined) {
    throw new Error('Unknown API')
  }

  //GET URI 전환 ( {:groupId} 등 변환 )
  // url = url.replace(/:(\S+)/, (match,$1)=>{
  //     const alt = params[$1];
  //     delete params[$1];
  //     return alt;
  // });
  url = url.replace(/{(\w+)}/g, (match, $1) => {
    const alt = params[$1]
    delete params[$1]
    delete parameter[$1]
    return alt
  })
  // console.log(url)

  return new Promise(function(resolve, reject) {
    // log(url, parameter, option.headers);
    log(url, parameter)

    axios[method](url, parameter, option)
      .then(function(res) {
        receiver(res, resolve)
      })
      .catch(function(err) {
        errorHandler(err, reject)
      })
  })
}

/**
 * Common response handler
 * @param {Object} res
 * @param {Function} callback
 */
const receiver = function(res, callback) {
  if (res.data) {
    const code = res.data['code']
    log(res.data)
    if (code === 1) {
      const data = res.data['data']
      typeof callback === 'function' && callback(data)

      //Token 갱신
      if (data && data.accessToken) {
        TOKEN = data.accessToken
      }
    } else {
      throw new Error(code)
    }
  }
}

/**
 * Common error handler
 * @param {Object} err
 * @param {Function} callback
 */
const errorHandler = function(err, callback) {
  console.error(err)
  const errorList = ErrorList
  const error = {}
  error.code = isNaN(parseInt(err.message))
    ? err.message
    : parseInt(err.message)
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
  } else {
    // window.sessionStorage.clear()
    // window.location.href = "/"
  }

  typeof callback === 'function' && callback(error)
}

export default sender
