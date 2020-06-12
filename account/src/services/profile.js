import Profile from '@/models/profile/Profile'
import { api } from '@/plugins/axios'

function getMyProfile() {
  return process.client && { ...$nuxt.$store.getters['auth/myProfile'] }
}

export default {
  getMyProfile,
  async auth() {
    if (Object.keys(getMyProfile()).length) return false
    const data = await api('GET_AUTH_INFO')
    $nuxt.$store.commit('auth/SET_MY_PROFILE', new Profile(data.userInfo))
  },
  async certification(form) {
    await api('ACCESS_AUTH', {
      route: { userId: form.uuid },
      params: form,
    })
    $nuxt.$store.commit('auth/SET_AUTH', true)
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
}
