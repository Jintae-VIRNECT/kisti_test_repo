import axios from 'axios'
import https from 'https'

const customAxios = axios.create({
  baseURL: process.env.API_GATEWAY_URL,
  timeout: process.env.API_TIMEOUT,
  headers: { 'Content-Type': 'application/json' },
  httpsAgent: new https.Agent({
    rejectUnauthorized: false,
  }),
})

customAxios.interceptors.request.use(req => {
  return req
})

export default customAxios
