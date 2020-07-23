import Axios from 'axios'

export default Axios.create({
  timeout: process.env.NODE_ENV === 'production' ? 2000 : 10000,
  withCredentials: false,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json',
  },
})
