import Profile from '@/models/profile/Profile'
import LoggedInDevice from '@/models/profile/LoggedInDevice'
import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'
import Cookies from 'js-cookie'

function getMyProfile() {
  return { ...store.getters['auth/myProfile'] }
}

export default {
  getMyProfile,
  async auth() {
    if (Object.keys(getMyProfile()).length) return false
    const data = await api('GET_AUTH_INFO')
    store.commit('auth/SET_MY_PROFILE', new Profile(data.userInfo))
  },
  async certification(form) {
    await api('ACCESS_AUTH', {
      route: { userId: form.uuid },
      params: form,
    })
    store.commit('auth/SET_AUTHENTICATED', true)
  },
  async updateMyProfile(form) {
    await api('UPDATE_USER_INFO', {
      route: { userId: getMyProfile().uuid },
      params: form,
    })
  },
  async updateMyImage(form) {
    if (form.profile) {
      const formData = new FormData()
      formData.append('profile', form.profile)

      await api('UPDATE_USER_IMAGE', {
        route: { userId: getMyProfile().uuid },
        params: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })
    } else {
      // 업로드했던 이미지 지우고 기본이미지로 수정
      await api('UPDATE_USER_IMAGE', {
        route: { userId: getMyProfile().uuid },
        params: { profile: null },
      })
    }
  },
  async getLoggedInDeviceList(searchParams) {
    const { accessInfoList, pageMeta } = await api('GET_ACCESS_LOGS', {
      route: { userId: getMyProfile().uuid },
      params: {
        size: 10,
        ...searchParams,
      },
    })
    return {
      list: accessInfoList.map(accessInfo => new LoggedInDevice(accessInfo)),
      total: pageMeta.totalElements,
    }
  },
  async secession(form) {
    const { uuid, email } = getMyProfile()
    form.uuid = uuid
    form.email = email
    const data = await api('SECESSION', { params: form })

    if (/\.?virnect\.com/.test(location.href)) {
      Cookies.remove('accessToken', { domain: '.virnect.com' })
      Cookies.remove('refreshToken', { domain: '.virnect.com' })
    } else {
      Cookies.remove('accessToken')
      Cookies.remove('refreshToken')
    }
    location.href = '/'
    return data
  },
}
