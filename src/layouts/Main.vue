<template>
  <transition name="app-fade" mode="out-in">
    <router-view :showStatus="showStatus" :auth="AUTH" />
  </transition>
</template>

<script>
import { onMounted } from '@vue/composition-api'
import Vue from 'vue'
import api from 'api/axios'
import auth from '@virnect/platform-auth'
export default {
  async beforeRouteEnter(to, from, next) {
    let res = await api.getUrls()
    const environmentCss = 'font-size: 1.2rem;'
    console.log('%cprocess env: %s', environmentCss, res.env)
    Vue.prototype.$urls = res
    Vue.prototype.$env = res.env
    await auth.init({ env: res.env, urls: res, timeout: res.timeout })
    next()
  },
  setup(props, { root }) {
    const AUTH = auth
    const showStatus = {
      login: false,
      profile: false,
      language: true,
      portal: false,
    }

    onMounted(() => {
      // IE 체크
      if (
        navigator.userAgent.indexOf('MSIE ') > 0 ||
        !!navigator.userAgent.match(/Trident.*rv:11\./)
      ) {
        root.$router.replace('/nobrowser')
      }
      if (root.$env === 'onpremise') {
        root.$store.dispatch('SET_CUSTOM')
      }
    })

    return {
      AUTH,
      showStatus,
    }
  },
}
</script>
