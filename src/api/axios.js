import Axios from 'axios'
import urls from '@/server/urls'

export default Axios.create({
  timeout: process.env.NODE_ENV === 'production' ? 2000 : 10000,
  withCredentials: false,
  headers: {
    'Access-Control-Allow-Origin': urls.api[process.env.TARGET_ENV],
    'Content-Type': 'application/json',
  },
})
