import https from 'https'
import Cookies from 'js-cookie'
import URI from '@/api/uri'
import { context } from '@/plugins/context'

let axios = null
let fileAxios = null
/**
 * Api gateway
 * @param {String} name
 * @param {Object} option
 */
export async function api(name, option = {}) {
  if (!URI[name]) {
    throw new Error(`API not found '${name}'`)
  }
  let [method, uri] = URI[name]
  let { route, params, headers } = option

  // replace route
  method = method.toLowerCase()
  uri = !route
    ? uri
    : Object.entries(route).reduce((u, q) => {
        return u.replace(`{${q[0]}}`, q[1])
      }, uri)

  // filter ALL -> null
  if (params && params.filter && params.filter === 'ALL') {
    delete params.filter
  }

  // GET, DELETE
  if (method === 'get') params = { params }
  if (method === 'delete') params = { data: params }

  // default header
  const accessToken = process.client
    ? Cookies.get('accessToken')
    : headers && headers.cookie.match(/accessToken=(.*?)(?![^;])/)[1]
  if (accessToken) {
    axios.defaults.headers.common = {
      Authorization: `Bearer ${accessToken}`,
    }
  }

  try {
    const response = await axios[method](uri, params, { headers })
    const { code, data, message, service } = response.data

    if (code === 200) {
      return data
    } else if (code === 8003 || code === 8005) {
      if (process.client) location.href = context.$url.console
      throw new Error(`${code}: ${message}`)
    } else {
      const error = new Error(`${code}: ${message}`)
      if (code) error.code = code
      console.error(error)
      throw error
    }
  } catch (e) {
    if (context.$config.DEBUG) console.error(`URL: ${uri}`)
    // timeout
    if (e.code === 'ECONNABORTED') {
      e.statusCode = 504
      context.error(e)
    }
    if (process.client) $nuxt.$loading.fail()
    else context.error(e)
    throw e
  }
}

/**
 * fileDownloadApi
 * @param {String} name
 * @param {Object} option
 * @returns {Object} url 파일 다운로드, fileName 파일명
 */
export async function fileDownloadApi(name, option = {}) {
  if (!URI[name]) {
    throw new Error(`API not found '${name}'`)
  }
  let [method, uri] = URI[name]
  let {
    route,
    params,
    headers,
    responseType = undefined,
    contentType = fileAxios.defaults.headers['Content-Type'],
  } = option

  // replace route
  method = method.toLowerCase()
  uri = !route
    ? uri
    : Object.entries(route).reduce((u, q) => {
        return u.replace(`{${q[0]}}`, q[1])
      }, uri)

  // filter ALL -> null
  if (params && params.filter && params.filter === 'ALL') {
    delete params.filter
  }

  // GET, DELETE
  if (method === 'get') params = { params }
  if (method === 'delete') params = { data: params }

  // default header
  const accessToken = process.client
    ? Cookies.get('accessToken')
    : headers && headers.cookie.match(/accessToken=(.*?)(?![^;])/)[1]
  if (accessToken) {
    fileAxios.defaults.headers.common = {
      Authorization: `Bearer ${accessToken}`,
    }
  }
  if (responseType) {
    fileAxios.defaults.responseType = responseType
  }
  fileAxios.defaults.headers['Content-Type'] = contentType

  try {
    const res = await fileAxios[method](uri, params, {
      headers,
    })

    const contentDisposition = res.headers['content-disposition']
    let fileName = ''

    if (typeof res.data === 'object') {
      if (contentDisposition) {
        const [fileNameMatch] = contentDisposition
          .split(';')
          .filter(str => str.includes('filename'))

        if (fileNameMatch) [, name] = fileNameMatch.split('=')
        fileName = name.replaceAll('"', '')
      }
      return {
        url: window.URL.createObjectURL(res.data),
        fileName,
      }
    } else if (res.data.code === 8003 || res.data.code === 8005) {
      if (process.client) location.href = context.$url.console
      throw new Error(`${res.data.code}: ${res.data.message}`)
    } else {
      const error = new Error(`${res.data.code}: ${res.data.message}`)
      if (res.data.code) error.code = res.data.code
      console.error(error)
      throw error
    }
  } catch (e) {
    if (context.$config.DEBUG) console.error(`URL: ${uri}`)
    // timeout
    if (e.code === 'ECONNABORTED') {
      e.statusCode = 504
      context.error(e)
    }
    if (process.client) $nuxt.$loading.fail()
    else context.error(e)
    throw e
  }
}

export function allSettled(iterable) {
  const onFulfill = v => ({ status: 'fulfilled', value: v })
  const onReject = v => ({ status: 'rejected', reason: v })

  return Promise.all(
    Array.from(iterable).map(p =>
      Promise.resolve(p).then(onFulfill).catch(onReject),
    ),
  )
}

export default function ({ $config, $axios }, inject) {
  // Create a custom axios instance
  axios = $axios.create({
    baseURL: $config.API_GATEWAY_URL,
    timeout: $config.API_TIMEOUT,
    headers: { 'Content-Type': 'application/json' },
    httpsAgent: new https.Agent({
      rejectUnauthorized: false,
    }),
    withCredentials: /(staging|production)/.test($config.VIRNECT_ENV),
  })
  fileAxios = $axios.create({
    baseURL: $config.API_GATEWAY_URL,
    timeout: 600000,
    headers: { 'Content-Type': 'application/octet-stream' },
    httpsAgent: new https.Agent({
      rejectUnauthorized: false,
    }),
    withCredentials: /(staging|production)/.test($config.VIRNECT_ENV),
  })

  /**
   * Api gateway
   * @param {String} name
   * @param {Object} option
   */
  inject('api', api)
  inject('fileDownloadApi', fileDownloadApi)
}
