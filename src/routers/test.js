import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
        path: '/test/:tab?',
        name: 'test',
        component: () => import('components/test/Test')
    },
    {
        path: '*',
        redirect: 'test'
    }
  ]
})
