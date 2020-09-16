import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'
import { api } from '@/plugins/axios'

export default async function({ req, redirect, error, $config }) {
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
      await api('GET_AUTH_INFO', { headers: req.headers })
    } catch (e) {
      // 비정상 토큰
      if (/^Error: (8003|8005)/.test(e)) {
        return redirect(
          `${LOGIN_SITE_URL}?continue=${encodeURIComponent(
            req.headers.referer || req.headers.host,
          )}`,
        )
      } else if (e.code === 'ECONNABORTED') {
        e.statusCode = 504
      }
      error({ statusCode: e.statusCode, message: e.message })
    }
  }
}
