import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/account',
      name: 'login',
    },
    {
      path: '*',
      redirect: 'login',
    },
  ],
})
