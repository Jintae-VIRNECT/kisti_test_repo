import Vue from 'vue'
import VueRouter from 'vue-router'
const DashboardComponent = () => import('components/layout/DashBoardLayout')

Vue.use(VueRouter)

export default new VueRouter({
  name: 'router',
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'main',
      component: DashboardComponent,
    },
    {
      path: '*',
      redirect: '/',
    },
  ],
})
