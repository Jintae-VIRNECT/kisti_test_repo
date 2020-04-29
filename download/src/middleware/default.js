function toLogin(req, redirect) {
  return redirect(
    `${process.env.LOGIN_SITE_URL}?continue=${encodeURIComponent(
      req.headers.referer || req.headers.host,
    )}`,
  )
}

export default async function({ req, store, redirect }) {
  if (process.server) {
    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return toLogin(req, redirect)
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
    } catch (e) {
      // 토큰 만료됨
      if (/^Error: 8005/.test(e)) {
        return toLogin(req, redirect)
      } else {
        throw e
      }
    }
  }
}
