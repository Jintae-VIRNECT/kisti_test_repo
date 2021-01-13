export default function({ req, store, redirect }) {
  // 사용자가 인증을 하지 않은 경우.
  const requireAuth = !store.state.auth.authenticated
  const authAlready = req && /\/profile$/.test(req.headers.referer)
  if (requireAuth && !authAlready) {
    return redirect('/profile/certification')
  }
}
