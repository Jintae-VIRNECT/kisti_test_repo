import axios from 'axios'

const customAxios = axios.create({
  baseURL: process.env.USER_API_URL,
  timeout: process.env.API_TIMEOUT,
  headers: { 'Content-Type': 'application/json' },
})

customAxios.interceptors.request.use(function(req) {
  if (/^\/workspace/.test(req.url)) {
    req.baseURL = process.env.WORKSPACE_API_URL
  } else if (/^\/licenses/.test(req.url)) {
    req.baseURL = process.env.LICENSE_API_URL
  }
  return req
})

export default customAxios
