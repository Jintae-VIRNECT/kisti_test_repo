import Profile from '@/models/profile/Profile'
import api from '@/api/gateway'

function getMyProfile() {
  const profile = $nuxt.$store.getters['auth/myProfile']
  return new Profile(profile)
}

export default {
  getMyProfile,
  async getAuthInfo() {
    const data = await api('GET_AUTH_INFO')
    console.log(data)
    return data
  },
  async certification(form) {
    const data = await api('ACCESS_AUTH', {
      route: { userId: form.uuid },
      params: form,
    })
    $nuxt.$store.commit('auth/SET_MY_PROFILE', data.userInfo)
    $nuxt.$store.commit('auth/SET_AUTH', true)
  },
  async updateMyProfile(form) {
    await api('UPDATE_USER_INFO', {
      route: { userId: getMyProfile().uuid },
      params: form,
    })
  },
  async updateMyImage(form) {
    const formData = new FormData()
    formData.append('profile', form.profile)

    await api('UPDATE_USER_IMAGE', {
      route: { userId: getMyProfile().uuid },
      params: formData,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}
