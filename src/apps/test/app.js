import "es6-promise/auto";
import "babel-polyfill";

import Vue from "vue";
import ElementUI from "element-ui";
import moment from "moment";
import VueMoment from "vue-moment";
import Router from "routers/test";

import App from "./app.vue";
import i18n from "plugins/service/i18n";

Vue.use(ElementUI, {
  i18n: (key, value) => i18n.t(key, value)
});

Vue.use(VueMoment, {
  moment
});

export default new Vue({
  el: "#container",
  router: Router,
  i18n,
  render: h => h(App)
});