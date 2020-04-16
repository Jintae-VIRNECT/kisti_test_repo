import axios from 'axios'

const customAxios = axios.create({
  baseURL: process.env.API_GATEWAY_URL,
  timeout: process.env.API_TIMEOUT,
  headers: { 'Content-Type': 'application/json' },
})

customAxios.interceptors.request.use(req => {
  return req
})

export default customAxios
