import axios from 'axios'

const smicId = '4d6eab0860969a50acbfa4599fbb5ae8'
const customAxios = axios.create({
  baseURL: process.env.USER_API_URL,
  timeout: 3000,
  headers: { 'Content-Type': 'application/json' },
})

customAxios.interceptors.request.use(function(req) {
  if (/^@workspace/.test(req.url)) {
    req.baseURL = process.env.WORKSPACE_API_URL
    req.url = req.url.replace('@workspace', `/workspaces/${smicId}`)
  } else if (/^@contents/.test(req.url)) {
    req.baseURL = process.env.CONTENT_API_URL
    req.url = req.url.replace('@contents', '/contents')
  } else if (/^@process/.test(req.url)) {
    req.baseURL = process.env.CONTENT_API_URL
    req.url = req.url.replace('@process', '/processes')
  }
  return req
})

export default customAxios
