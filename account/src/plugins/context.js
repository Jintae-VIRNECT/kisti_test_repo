export let context = null
export let app = null
export let store = null

export default function(con) {
  context = con
  app = con.app
  store = con.store
}
