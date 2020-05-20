import Vue from 'vue'
import VueRouter from 'vue-router'
const LoginComponent = () => import('components/account/AccountLogin')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/account',
      name: 'login',
      component: LoginComponent,
    },
    {
      path: '*',
      redirect: 'login',
    },
  ],
})
