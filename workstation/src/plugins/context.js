import { Url } from 'WC-Modules/javascript/api/virnectPlatform/urls'

export let context = null
export let app = null
export let store = null
export let url = null

export default function(con, inject) {
  context = con
  app = con.app
  store = con.store
  url = new Url(con.$config.VIRNECT_ENV)

  inject('url', url)
  inject(
    'defaultWorkspaceProfile',
    require('assets/images/workspace-profile.png'),
  )
  inject(
    'defaultUserProfile',
    require('assets/images/icon/ic-user-profile.svg'),
  )
}
