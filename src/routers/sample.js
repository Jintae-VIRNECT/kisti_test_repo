import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/sample/:tab?',
      name: 'sample',
      component: () => import('components/sample/Sample'),
    },
    {
      path: '*',
      redirect: 'sample',
    },
  ],
})
