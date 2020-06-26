import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default async function({ req, store, redirect, $config }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

  if (process.server) {
    const LOGIN_SITE_URL = urls.console[$config.TARGET_ENV]
    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${LOGIN_SITE_URL}?continue=${encodeURIComponent(
          req.headers.referer || req.headers.host,
        )}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
    } catch (e) {
      // 비정상 토큰
      if (/^Error: (8003|8005)/.test(e)) {
        return redirect(
          `${LOGIN_SITE_URL}?continue=${encodeURIComponent(
            req.headers.referer || req.headers.host,
          )}`,
        )
      } else {
        throw e
      }
    }

    // 홈이 없어서 개인정보로 임시 리다이렉트
    if (req.url === '/') {
      return redirect('/profile')
    }
  }
}
