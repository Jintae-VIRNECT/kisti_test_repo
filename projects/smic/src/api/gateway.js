import URI from './uri'
import Vue from 'vue'

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
  let { route, params } = option

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
  params = method === 'post' ? params : { params }

  const response = await Vue.axios[method](uri, params)
  const { code, data, message } = response.data

  if (code === 200) {
    return data
  } else {
    throw new Error(`${code}: ${message}`)
  }
}
