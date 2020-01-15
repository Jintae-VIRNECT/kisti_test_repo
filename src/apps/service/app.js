import "es6-promise/auto";
import "babel-polyfill";

import Vue from "vue";
import ElementUI from "element-ui";
import moment from "moment";
import VueMoment from "vue-moment";
import Router from "routers/service";
import Store from "stores/service/store";

import App from "./app.vue";
import globalMixin from "mixins/global";
import i18n from "plugins/service/i18n";
import remoteSDK from "plugins/service/remote-sdk";
import Vue2Scrollbar from 'plugins/service/scrollbar'
import Axios from "axios";

import openvidu from 'plugins/service/openvidu'

// const axios = Axios.create({
//   timeout: 10000,
//   withCredentials: false,
//   headers: {
//     "Access-Control-Allow-Origin": "https://virnectremote.com",
//     "Content-Type": "application/json",
//     client: "web"
//   }
// });

// function applyScript(url) {
//   return new Promise((resolve, reject) => {
//     const s = document.createElement("script");
//     s.type = "text/javascript";
//     s.src = url;
//     s.async = true;
//     s.defer = true;
//     s.onload = () => {
//       Vue.use(remoteSDK, {
//         Store
//       });
//       resolve();
//     };
//     document.body.appendChild(s);
//   });
// }

// (async () => {
//   const res = await axios.post("/urls");
//   await applyScript(res.data.REMOTE_SDK_URL);
// })();


Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
});

Vue.use(VueMoment, {
  moment
});

Vue.mixin(globalMixin);
Vue.use(Vue2Scrollbar)

Vue.use(openvidu, { Store })

const EventBus = new Vue();
Vue.prototype.$eventBus = EventBus;
Vue.prototype.version = "2.0.0";

export default new Vue({
  el: "#container",
  router: Router,
  store: Store,
  i18n,
  render: h => h(App)
});

setTimeout(
  console.log.bind(
    console,
    `%c VIRNECT Remote Service `,
    "padding:4px 18px;background:linear-gradient(to right, #0064ff, #6700ff);font-size:32px;color:#fff;border-radius:15px"
  )
);
