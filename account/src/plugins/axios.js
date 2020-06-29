import https from 'https'
import Cookies from 'js-cookie'
import URI from '@/api/uri'
import { url } from '@/plugins/context'

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

  if (process.client && $nuxt.$loading.start) $nuxt.$loading.start()
  try {
    // payletter api
    if (/^\/billing/.test(uri)) {
      if (method === 'get') params.params.sitecode = 1
      else if (method === 'delete') params.data.sitecode = 1
      else params.sitecode = 1

      const response = await axios[method](uri, params, { headers })
      const { data, result } = response.data
      if (process.client) $nuxt.$loading.finish()

      if (result.code === 0) {
        return data
      } else {
        const error = new Error(`${result.code}: ${result.message}`)
        console.error(error)
        throw error
      }
    }
    // platform api
    else {
      const response = await axios[method](uri, params, { headers })
      const { code, data, message, service } = response.data
      if (process.client) $nuxt.$loading.finish()

      if (code === 200) {
        return data
      } else if (code === 8003 || code === 8005) {
        if (process.client) location.href = url.console
        throw new Error(`${code}: ${message}`)
      } else {
        const error = new Error(`${code}: ${message}`)
        console.error(error)
        throw error
      }
    }
  } catch (e) {
    if (process.client) {
      $nuxt.$loading.fail()
      $nuxt.$loading.finish()
    }
    console.error(`URL: ${uri}`)
    throw e
  }
}

export default function({ $config, $axios }, inject) {
  // Create a custom axios instance
  axios = $axios.create({
    baseURL: $config.API_GATEWAY_URL,
    timeout: $config.API_TIMEOUT,
    headers: { 'Content-Type': 'application/json' },
    httpsAgent: new https.Agent({
      rejectUnauthorized: false,
    }),
  })

  inject('api', api)
}
