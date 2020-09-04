// import DashboardLayout from '@/components/layout/DashBoardLayout'
import Contents from '@/components/contents/Contents'

import Vue from 'vue'
import VueRouter from 'vue-router'
const Dashboard = () => import('components/contents/Dashboard')
const FileManage = () => import('components/contents/FileManage')

Vue.use(VueRouter)

export default new VueRouter({
  routes: [
    {
      path: '/',
      component: Contents,
      redirect: '/',
      children: [
        {
          path: '/',
          name: 'dashboard',
          component: Dashboard,
        },
        {
          path: 'filemanage',
          name: 'filemanage',
          component: FileManage,
        },
      ],
    },
  ],
})
