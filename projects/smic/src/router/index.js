import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '@/views/Home.vue'
import Member from '@/views/members/Member.vue'
import MemberList from '@/views/members/MemberList.vue'
import MemberNew from '@/views/members/MemberNew.vue'

import Content from '@/views/contents/Content.vue'
import ContentList from '@/views/contents/ContentList.vue'
import ContentDetail from '@/views/contents/ContentDetail.vue'
import ContentNew from '@/views/contents/ContentNew.vue'

import Process from '@/views/process/Process.vue'
import ProcessList from '@/views/process/ProcessList.vue'
import ProcessDetail from '@/views/process/ProcessDetail.vue'
import ProcessNew from '@/views/process/ProcessNew.vue'

import Issue from '@/views/issue/Issue'

import User from '@/views/User.vue'

import NotFound404 from '@/views/NotFound404.vue'

import store from '@/store'
// import EventBus from '@/utils/eventBus.js'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: Home,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: '/users',
    component: User,
    meta: {
      preventAfterLoggedIn: true,
    },
  },
  {
    path: '/issue',
    component: Issue,
    meta: {
      requiresAuth: true,
    },
  },
  {
    path: '/members',
    component: Member,
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: '',
        component: MemberList,
        props: route => ({ query: route.query }),
      },
      {
        path: 'new',
        component: MemberNew,
      },
    ],
  },
  {
    path: '/contents',
    component: Content,
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: '',
        component: ContentList,
      },
      {
        path: 'new',
        component: ContentNew,
      },
      {
        path: ':id',
        component: ContentDetail,
      },
    ],
  },
  {
    path: '/process',
    component: Process,
    meta: {
      requiresAuth: true,
    },
    children: [
      {
        path: '',
        component: ProcessList,
      },
      {
        path: 'new',
        component: ProcessNew,
      },
      {
        path: ':id',
        component: ProcessDetail,
      },
    ],
  },
  {
    path: '*',
    component: NotFound404,
  },
]

const router = new VueRouter({
  mode: 'history',
  // base: process.env.BASE_URL,
  routes,
})

router.beforeEach((to, from, next) => {
  if (from.path !== '/' && to.path === from.path) return

  // auth check
  const requiresAuth = to.matched.find(record => record.meta.requiresAuth)

  let destination = to.path

  const user = store.state ? store.state.user : null
  if (requiresAuth) {
    if (user && user.isLoggedIn) {
      // 마지막 접근루트로 이동
      let lastAccessPath = store.getters.getLastAccessPath || to.path
      destination = lastAccessPath
      store.commit('USER_SET_LAST_ACCESS_PATH', { path: null })
    } else {
      Vue.swal.fire({
        type: 'error',
        title: '로그인이 필요합니다',
        toast: true,
        position: 'bottom-end',
        showConfirmButton: false,
        timer: 1500,
      })
      destination = '/users'
      store.commit('USER_SET_LAST_ACCESS_PATH', {
        path: to.path,
      })
    }
  } else if (destination === '/users' && user && user.isLoggedIn) {
    destination = '/'
  }

  if (destination !== to.path) {
    next({
      path: destination || to.path,
    })
  } else {
    next()
  }
})

export default router
