<template>
  <transition name="app-fade" mode="out-in">
    <router-view :showStatus="showStatus" :auth="auth" />
  </transition>
</template>

<script>
import Vue from 'vue'
import api from 'api/axios'
import auth from '@virnect/platform-auth'
import store from '@/store/index'
export default {
  async beforeRouteEnter(to, from, next) {
    if (to.query.lang) {
      const lang = to.query.lang
      await store.dispatch('CHANGE_LANG', lang)
    }
    let res = await api.getUrls()
    const environmentCss = 'font-size: 1.2rem;'
    console.log('%cprocess env: %s', environmentCss, res.env)
    Vue.prototype.$urls = res
    Vue.prototype.$env = res.env
    await auth.init({ env: res.env, urls: res, timeout: res.timeout })
    next()
  },
  data() {
    return {
      auth,
      showStatus: {
        login: true,
        profile: false,
        language: true,
      },
    }
  },
  mounted() {
    // IE 체크
    if (
      navigator.userAgent.indexOf('MSIE ') > 0 ||
      !!navigator.userAgent.match(/Trident.*rv:11\./)
    ) {
      this.$router.replace('/nobrowser')
    }
    if (this.$env === 'onpremise') {
      this.$store.dispatch('SET_CUSTOM')
    }
  },
}
</script>

<style lang="scss" scoped></style>
