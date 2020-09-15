<template>
  <div>
    <header>
      <the-header :showSection="showSection" :auth="auth">
        <template slot="subTitle">{{ $t('menu.account') }}</template>
      </the-header>
    </header>
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

export default {
  middleware: 'default',
  components: {
    TheHeader,
  },
  head() {
    return {
      title: `VIRNECT | ${this.$t('menu.account')}`,
    }
  },
  data() {
    return {
      showSection: {
        login: false,
        lang: false,
        link: true,
        profile: true,
      },
    }
  },
  computed: {
    auth() {
      return this.$store.getters['auth/auth']
    },
  },
  beforeMount() {
    // 콘솔 표시
    console.log(
      `%cVirnect Account v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.TARGET_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
    this.$store.dispatch('auth/getAuth', this.$config.TARGET_ENV)
  },
}
</script>
