import Cookies from 'js-cookie'

export default function({ req, redirect }) {
  // 사용자가 로그인을 하지 않은 경우.
  if (
    process.server &&
    (!req.headers.cookie || !req.headers.cookie.match('accessToken='))
  ) {
    return redirect(
      `${process.env.LOGIN_SITE_URL}?continue=${req.headers.host}`,
    )
  }
  // 임시
  if (process.server && req.url === '/') {
    return redirect('/coupon')
  }
}
