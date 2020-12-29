<template>
  <div>
    <the-header :showSection="showSection" :auth="auth">
      <template slot="subTitle">
        {{ $t('home.title') }}
      </template>
    </the-header>
    <main>
      <nuxt />
    </main>
    <the-footer />
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'

export default {
  middleware: ['default'],
  components: {
    TheHeader,
    TheFooter,
  },
  head() {
    return {
      title: `VIRNECT | ${this.$t('home.title')}`,
      htmlAttrs: {
        lang: this.$i18n.locale,
      },
    }
  },
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
    }
  },
  computed: {
    auth() {
      return this.$store.getters['auth/auth']
    },
  },
  mounted() {
    // 콘솔 표시
    console.log(
      `%cVirnect Download v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.VIRNECT_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)

    // 언어 선택 쿼리
    const lang = this.$route.query.lang
    if (this.$i18n.locales.includes(lang)) {
      this.$store.dispatch('CHANGE_LANG', lang)
      this.$i18n.locale = lang
    }

    this.$store.dispatch('auth/getAuth')
  },
}
</script>

<style lang="scss">
body .sub-title {
  font-size: 18px;
}
main {
  margin-top: 60px;
}
</style>
