import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default async function({ req, store, redirect, error, $config }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

  if (process.server) {
    const LOGIN_SITE_URL = urls.console[$config.TARGET_ENV]
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
        `${LOGIN_SITE_URL}?continue=${encodeURIComponent(
          req.headers.referer || req.headers.host,
        )}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
      const myWorkspaces = store.getters['auth/myWorkspaces']
      if (myWorkspaces.length) {
        // 마지막 워크스페이스 확인
        const lastWorkspace = req.headers.cookie.match(
          /activeWorkspace=([0-9a-zA-Z]+)/,
        )
        const activeWorkspace =
          lastWorkspace &&
          myWorkspaces.find(workspace => workspace.uuid === lastWorkspace[1])
            ? lastWorkspace[1]
            : myWorkspaces[0].uuid
        store.commit('auth/SET_ACTIVE_WORKSPACE', activeWorkspace)
      } else {
        // 워크스페이스가 없는 경우
        return redirect('/start')
      }
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
