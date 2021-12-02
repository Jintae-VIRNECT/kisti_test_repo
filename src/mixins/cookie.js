import { domainRegex } from 'mixins/validate'

export const cookieOption = (urls, expire) => {
  const isDomain = urls.domain
    ? urls.domain
    : location.hostname.replace(/.*?\./, '')

  const URL = domainRegex.test(location.hostname) ? isDomain : location.hostname
  if (expire)
    return {
      secure: true,
      sameSite: 'None',
      expires: expire / 3600000,
      domain: URL,
    }
  else
    return {
      secure: true,
      sameSite: 'None',
      domain: URL,
    }
}
