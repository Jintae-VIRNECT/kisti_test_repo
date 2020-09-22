export let context = null
export let app = null
export let store = null
export let url = null

export default function(con, inject) {
  context = con
  app = con.app
  store = con.store

  inject('url', con.$config.URLS)
  inject(
    'defaultWorkspaceProfile',
    require('assets/images/workspace-profile.png'),
  )
  inject(
    'defaultUserProfile',
    require('assets/images/icon/ic-user-profile.svg'),
  )
}
