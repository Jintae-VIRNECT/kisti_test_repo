import Vue from 'vue'
import Toasted from 'vue-toasted'
import './style/vue-toasted.scss'

Vue.use(Toasted)

const callNotifyOption = {
  position: 'top-right',
  duration: 10000000000,
  fitToScreen: true,
  keepOnHover: true,
  type: 'notify',
}
Vue.toasted.register(
  'callNotify',
  payload => {
    let icon = payload.icon
    if (payload.type === 'info' || payload.type === 'license') {
      icon = require('assets/image/ic_system.svg')
    } else if (payload.type === 'fail') {
      icon = require('assets/image/ic_notice.svg')
    }
    let template = ``
    if (payload.type === 'license') {
      template = `
        <div class="toasted__buttons">
          <button class="btn small" @click="${() => {
            payload.onClick()
          }}">라이선스 구매</button>
        </div>`
    } else if (payload.type === 'invite') {
      template = `
      <div class="toasted__buttons">
        <button class="btn small" onClick="${payload.accept()}">수락</button>
        <button class="btn small sub" onClick="${payload.deny()}">거절</button>
      </div>`
    } else if (payload.type === 'file') {
      template = `
      <div class="toasted__buttons">
        <button class="btn small sub filelink">${payload.filename}</button>
      </div>`
    }
    return (
      `
      <figure>
        <div class="toasted__thumb ${payload.type}">
          <img src="${icon}" />
        </div>
        <figcaption>
          <p class="toasted__info ${payload.type}">${payload.info}</p>
          <p class="toasted__description">${payload.description}</p>
      ` +
      template +
      `
        </figcaption>
      </figure>`
    )
  },
  callNotifyOption,
)

export default {}
