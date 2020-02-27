import URI from './uri'
import Vue from 'vue'

/**
 * Api gateway
 * @param {String} name
 * @param {Object} option
 */
export default async function api(name, option) {
  let [method, uri] = URI[name]
  let { query, params } = option

  method = method.toLowerCase()
  params = method === 'post' ? params : { params }
  // replace query
  uri = !query
    ? uri
    : Object.entries(query).reduce((u, q) => {
        return u.replace(`{${q[0]}}`, q[1])
      }, uri)

  const response = await Vue.axios[method](uri, params)
  const { code, data, message } = response.data

  if (code === 200) {
    return data
  } else {
    throw new Error(message)
  }
}
