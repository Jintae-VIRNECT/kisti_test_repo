import Axios from 'axios'
import authHeader from './auth-header'
import API from './url';

const GATEWAY_API_URL = 'http://localhost:8073';

const axios = Axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

class UserService {
  getUserContent () {
    return axios.get(GATEWAY_API_URL + API.user.userInfo, { headers: authHeader() })
  }
}

export default new UserService()
