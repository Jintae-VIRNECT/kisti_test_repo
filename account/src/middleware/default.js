import api from '@/api/gateway'
import Profile from '@/models/profile/Profile'

export default async function({ req, store, redirect }) {
  if (process.server) {
    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${process.env.LOGIN_SITE_URL}?continue=${req.headers.host}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    const data = await api('GET_AUTH_INFO', { headers: req.headers })
    store.commit('auth/SET_MY_PROFILE', new Profile(data.userInfo))

    // 홈이 없어서 쿠폰으로 임시 리다이렉트
    if (process.server && req.url === '/') {
      return redirect('/coupon')
    }
  }
}
