import axios from 'axios'

export default axios.create({
	baseUrl: process.NODE_ENV == 'production' ? '' : 'http://localhost:8080',
})
