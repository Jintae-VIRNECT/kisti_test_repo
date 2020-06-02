export default async function({ req, store, redirect, error }) {
  // nuxt undefined url bug
  if (req && req.url.split('/').find(_ => _.match(/undefined|null/)))
    redirect('/')

  if (process.server) {
    // chrome only
    const hasChrome = req.headers['user-agent'].indexOf('Chrome') !== -1
    const hasEdge = req.headers['user-agent'].indexOf('Edge') !== -1
    if (!hasChrome || hasEdge) {
      return error({ message: 'BrowserNotSupport' })
    }

    // 사용자가 로그인을 하지 않은 경우.
    if (!req.headers.cookie || !req.headers.cookie.match('accessToken=')) {
      return redirect(
        `${process.env.LOGIN_SITE_URL}?continue=${encodeURIComponent(
          req.headers.referer || req.headers.host,
        )}`,
      )
    }

    // 토큰으로 내 정보 불러오기
    try {
      await store.dispatch('auth/getAuthInfo', { headers: req.headers })
      const myProfile = store.getters['auth/myProfile']
      const myWorkspaces = store.getters['auth/myWorkspaces']
      if (myWorkspaces.length) {
        await store.dispatch('workspace/getMyWorkspaces', myProfile.uuid)
        const myWorkspaces = store.getters['workspace/myWorkspaces']
        // 마지막 워크스페이스 확인
        const lastWorkspace = req.headers.cookie.match(
          /activeWorkspace=([0-9a-f]+)/,
        )
        const activeWorkspace =
          lastWorkspace &&
          myWorkspaces.find(workspace => workspace.uuid === lastWorkspace[1])
            ? lastWorkspace[1]
            : myWorkspaces[0].uuid
        store.commit('workspace/SET_ACTIVE_WORKSPACE', activeWorkspace)
      } else {
        // 워크스페이스가 없는 경우
        return redirect('/start')
      }
    } catch (e) {
      // 토큰 만료됨
      if (/^Error: 8005/.test(e)) {
        return redirect(
          `${process.env.LOGIN_SITE_URL}?continue=${encodeURIComponent(
            req.headers.referer || req.headers.host,
          )}`,
        )
      } else {
        throw e
      }
    }
  }
}
