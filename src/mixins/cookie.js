import { domainRegex } from 'mixins/validate'
const COOKIE_EXPIRE_UNIT = 60 * 60 * 24 //쿠키 expires 값은 24시간이 1 기준으로 들어가고, api응답값의 expireIn은 초단위로 오기 때문에 계산하기 위한 값

export const cookieOption = (urls, expire) => {
  const isDomain = urls.domain
    ? urls.domain
    : location.hostname.replace(/.*?\./, '')

  const URL = domainRegex.test(location.hostname) ? isDomain : location.hostname
  if (expire)
    return {
      secure: true,
      sameSite: 'None',
      expires: expire / COOKIE_EXPIRE_UNIT,
      domain: URL,
    }
  else
    return {
      secure: true,
      sameSite: 'None',
      domain: URL,
    }
}
