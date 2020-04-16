import Cookies from 'js-cookie'
import URI from './uri'
import axios from '@/plugins/axios'

/**
 * Api gateway
 * @param {String} name
 * @param {Object} option
 */
export default async function api(name, option = {}) {
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
  params = method !== 'get' ? params : { params }

  // default header
  const accessToken = process.client
    ? Cookies.get('accessToken')
    : headers && headers.cookie.match('accessToken=(.*?);')[1]
  if (accessToken) {
    axios.defaults.headers.common = {
      Authorization: `Bearer ${accessToken}`,
    }
  }

  if (process.client && $nuxt.$loading.start) $nuxt.$loading.start()
  try {
    const response = await axios[method](uri, params, { headers })
    const { code, data, message } = response.data
    if (process.client) $nuxt.$loading.finish()

    if (code === 200) {
      return data
    } else {
      console.error(`URL: ${uri}`)
      throw new Error(`${code}: ${message}`)
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
