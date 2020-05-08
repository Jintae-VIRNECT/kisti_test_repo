import Vue from 'vue'
import Toasted from 'vue-toasted'
import './style/vue-toasted.scss'

Vue.use(Toasted)

const callNotifyOption = {
  position: 'top-center',
  duration: 100000,
  action: {
    icon: 'close',
    onClick: (e, toastObject) => {
      toastObject.goAway(0)
    },
  },
  fitToScreen: true,
  keepOnHover: true,
  type: 'notify',
}
Vue.toasted.register(
  'callNotify',
  payload => {
    return `<figure>
        <div class="thumb"><img src="${payload.userThumb}" /></div>
        <figcaption>
            <p class="name">${payload.userName}</p>
            <p>${payload.message}</p>
        </figcaption>
      </figure>
      <span class="datetime">${payload.datetime}</span>`
  },
  callNotifyOption,
)

export default {}
