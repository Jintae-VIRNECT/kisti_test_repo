import axios from 'axios'

export default axios.create({
  baseURL: process.env.USER_API_URL,
})
