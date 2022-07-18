import { context, url } from '@/plugins/context'

export default async function({ req, store, redirect, error, $config }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

  if (process.server) {
    // onpremise
    if (context.$config.VIRNECT_ENV === 'onpremise') {
      const whiteList = ['/', '/profile/op', '/profile/certification']
      if (req.url === '/') redirect('/profile/op')
      if (req.url === '/profile') redirect('/profile/op')
      else if (!whiteList.includes(req.url)) error({ statusCode: 404 })
    }

    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${url.console}?continue=${url.account}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
    } catch (e) {
      // 비정상 토큰
      if (/^Error: (8003|8005)/.test(e)) {
        return redirect(
          `${url.console}?continue=${url.account}`,
        )
      } else if (e.code === 'ECONNABORTED') {
        e.statusCode = 504
      }
      error({ statusCode: e.statusCode, message: e.message })
    }

    // onrpemise
    if ($config.VIRNECT_ENV === 'onpremise') {
      await store.dispatch('layout/getWorkspaceSetting', {
        headers: req.headers,
      })
    }
  }
}
