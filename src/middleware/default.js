import { url } from '@/plugins/context'
import { api } from '@/plugins/axios'

export default async function ({ req, res, store, redirect, error, $config }) {
  if (process.server) {
    // onrpemise
    if ($config.VIRNECT_ENV === 'onpremise') {
      await store.dispatch('layout/getWorkspaceSetting', {
        headers: req.headers,
      })
    }

    // not support browser
    const isIE =
      req.headers['user-agent'].indexOf('MSIE ') !== -1 ||
      req.headers['user-agent'].indexOf('Trident/') !== -1
    const isOldEdge = req.headers['user-agent'].indexOf('Edge') !== -1
    if (isIE || isOldEdge) {
      return error({ message: 'BrowserNotSupport' })
    }

    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${url.console}?continue=${url.download}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await api('GET_AUTH_INFO', { headers: req.headers })
    } catch (e) {
      // 비정상 토큰
      if (/^Error: (8003|8005)/.test(e)) {
        return redirect(
          `${url.console}?continue=${url.download}`,
        )
      } else if (e.code === 'ECONNABORTED') {
        e.statusCode = 504
      }
      error({ statusCode: e.statusCode, message: e.message })
    }
  }
}
