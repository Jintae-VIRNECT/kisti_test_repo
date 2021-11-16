import https from 'https'
import Cookies from 'js-cookie'
import URI from '@/api/uri'
import { context } from '@/plugins/context'

let axios = null
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

  /**
   * Api gateway
   * @param {String} name
   * @param {Object} option
   */
  inject('api', api)
}
