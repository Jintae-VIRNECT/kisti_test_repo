import { url } from '@/plugins/context'

export default async function ({ req, res, store, redirect, error, $config }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

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
      return error({ type: 'browser', message: 'BrowserNotSupport' })
    }

    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${url.console}?continue=${encodeURIComponent(
          req.headers.referer || req.headers.host,
        )}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
      const myWorkspaces = store.getters['auth/myWorkspaces']
      if (!myWorkspaces.length) {
        // 워크스페이스가 없는 경우
        return redirect('/start')
      } else {
        let activeWorkspaceId = ''
        // 마지막 워크스페이스 쿠키 확인
        const lastWorkspace = req.headers.cookie.match(
          /activeWorkspace=([0-9a-zA-Z]+)/,
        )
        if (lastWorkspace) activeWorkspaceId = lastWorkspace[1]
        // 워크스페이스 선택 쿼리가 있을 경우
        const workspaceQuery = req.url.match(/[?%]workspace=([0-9a-zA-Z]+)/)
        if (workspaceQuery) activeWorkspaceId = workspaceQuery[1]
        // 워크스페이스가 존재하지 않으면 기본 워크스페이스
        if (
          !myWorkspaces.some(workspace => workspace.uuid === activeWorkspaceId)
        ) {
          activeWorkspaceId = myWorkspaces[0].uuid
        }
        store.commit('auth/SET_ACTIVE_WORKSPACE', activeWorkspaceId)

        // 게스트의 경우 토큰 삭제
        if (store.getters['auth/activeWorkspace'].role === 'GUEST') {
          res.setHeader('Set-Cookie', ['accessToken=null', 'refreshToken=null'])
          return redirect(
            `${url.console}?continue=${encodeURIComponent(
              req.headers.referer || req.headers.host,
            )}`,
          )
        }

        // onrpemise
        if ($config.VIRNECT_ENV === 'onpremise') {
          if (
            store.getters['auth/activeWorkspace'].role !== 'MASTER' &&
            req.url === '/workspace/setting'
          ) {
            return error({ statusCode: 404 })
          }
        }
      }
    } catch (e) {
      // 비정상 토큰
      if (/^Error: (8003|8005)/.test(e)) {
        return redirect(
          `${url.console}?continue=${encodeURIComponent(
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
