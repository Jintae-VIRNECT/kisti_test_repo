export let context = null
export let app = null
export let store = null
export let url = null

export default function(con, inject) {
  context = con
  app = con.app
  store = con.store
  url = con.$config.URLS

  inject('url', url)
}
