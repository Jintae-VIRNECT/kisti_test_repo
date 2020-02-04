import axios from 'axios'

const customAxios = axios.create({
  baseURL: process.env.USER_API_URL,
  timeout: 3000,
  headers: { 'Content-Type': 'application/json' },
})

customAxios.interceptors.response.use(
  function(res) {
    const { status, statusText } = res
    if (status !== 200) console.log('statusText : ', statusText)
    const { code, message } = res.data
    if (code !== 200) console.log('message : ', message)

    return res
  },
  function(error) {
    console.log('error : ', error)
    return Promise.reject(error)
  },
)

export default customAxios
