import { url } from '@/plugins/context'

export default async function({ req, store, redirect, error }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

  if (process.server) {
    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${url.console}?continue=${encodeURIComponent(
          req.headers.referer || req.headers.host + req.url,
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
          `${url.console}?continue=${encodeURIComponent(
            req.headers.referer || req.headers.host,
          )}`,
        )
      } else {
        error(e)
      }
    }
  }
}
