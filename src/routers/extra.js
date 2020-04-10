import Vue from 'vue'
import VueRouter from 'vue-router'
const SupportComponent = () => import('components/support/SupportComponent')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/support',
      name: 'support',
      component: SupportComponent,
    },
  ],
})
