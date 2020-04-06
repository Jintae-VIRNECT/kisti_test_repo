import axios from 'axios'

const customAxios = axios.create({
  baseURL: process.env.USER_API_URL,
  timeout: 3000,
  headers: { 'Content-Type': 'application/json' },
})

customAxios.interceptors.request.use(function(req) {
  if (/^\/workspace/.test(req.url)) {
    req.baseURL = process.env.WORKSPACE_API_URL
  } else if (/^\/contents/.test(req.url)) {
    req.baseURL = process.env.CONTENT_API_URL
  } else if (/^\/processes/.test(req.url)) {
    req.baseURL = process.env.PROCESS_API_URL
  }
  return req
})

export default customAxios
