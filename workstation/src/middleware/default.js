export default async function({ req, store, redirect }) {
  if (process.server) {
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
      await store.dispatch('workspace/getActiveWorkspaceInfo', {
        headers: req.headers,
        route: {
          workspaceId: store.getters['auth/myWorkspaces'][0].uuid,
        },
      })
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
