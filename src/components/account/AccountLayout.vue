<template>
  <div class="account-wrapper" android:windowSoftInputMode="adjustResize">
    <account-header v-if="false"></account-header>

    <router-view></router-view>
    <!-- <component :is="component"></component> -->
  </div>
</template>

<script>
// import { mapGetters } from 'vuex'
// import { sessionStorage } from 'utils/storage'
import AccountHeader from './header/AccountHeader'
// import langMixin from 'mixins/language'

export default {
  name: 'AccountLayout',
  beforeRouteEnter(to, from, next) {
    // const account = sessionStorage.getItem('account')
    // if (account && account.sId) {
    //   // 로그인 상태 시 서비스로 이동.
    //   location.replace('/service')
    //   return
    // } else {
    //   // 비 로그인 상태 시
    //   sessionStorage.clear()
    //   next()
    // }
  },
  // mixins: [langMixin],
  components: {
    AccountHeader,
  },
  data() {
    return {
      component: null,
      name: '',
    }
  },
  computed: {
    // ...mapGetters(['isLogin']),
  },
  watch: {
    // isLogin(value) {
    //   if (value === true) {
    //     this.$router.push({ name: 'home' })
    //   }
    // },
  },

  /* Lifecycles */
  created() {
    // if (
    //   this.$route.query &&
    //   this.$route.query.lang &&
    //   this.mx_languages.findIndex(lang => lang === this.$route.query.lang) > -1
    // ) {
    //   this.mx_changeLanguage(this.$route.query.lang)
    // } else {
    //   this.mx_changeLanguage()
    // }
  },
}
</script>
<style lang="scss" src="assets/style/account.scss"></style>
