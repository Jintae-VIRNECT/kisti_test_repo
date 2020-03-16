import Axios from 'axios';
import API from './url';
import {resolveLocale} from "bootstrap-vue/esm/utils/date";

const GATEWAY_API_URL = 'http://localhost:8073';

const axios = Axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

class AuthService {
  login(user) {
    return axios
      .post(GATEWAY_API_URL + API.auth.login, {
        email: user.email,
        password: user.password
      })
      .then(this.handleResponse)
      .then(response => {
        const {data} = response;
        console.log(data);
        if (data.accessToken) {
          localStorage.setItem('user', JSON.stringify(data))
        }
        return data
      })
  }

  logout() {
    localStorage.removeItem('user')
  }

  register(user) {
    return axios.post(GATEWAY_API_URL + API.auth.register, {
      username: user.username,
      email: user.email,
      password: user.password
    })
  }

  emailAuth(email) {
    return axios
      .post(GATEWAY_API_URL+API.auth.emailAuth, {
        email
      })
      .then(this.handleResponse)
      .then(response => {
        const {data} = response.data;
        return data
      })
  }

  handleResponse(response) {
    const {data} = response;
    if (response.status !== 200 || data.code !== 200) {
      localStorage.removeItem('user');
      location.reload(true);

      const error = data.message;
      return Promise.reject(error)
    }

    return Promise.resolve(data);
  }
}

export default new AuthService()
