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
    const { code, data, message } = response.data

    if (code === 200) {
      return data
    } else {
      throw new Error(`${code}: ${message}`)
    }
  } catch (e) {
    console.error(`URL: ${uri}`)
    // timeout
    if (e.code === 'ECONNABORTED') {
      e.type = 'network'
      e.statusCode = 504
      context.error(e)
    }
    if (process.client) $nuxt.$loading.fail()
    else context.error(e)
    throw e
  }
}

export default function ({ $config, $axios }, inject) {
  // Create a custom axios instance
  axios = $axios.create({
    baseURL: $config.API_GATEWAY_URL,
    timeout: $config.API_TIMEOUT,
    withCredentials: /(staging|production)/.test($config.VIRNECT_ENV),
    headers: { 'Content-Type': 'application/json' },
    httpsAgent: new https.Agent({
      rejectUnauthorized: false,
    }),
  })

  inject('api', api)
}
