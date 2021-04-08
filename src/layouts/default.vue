<template>
  <div>
    <VirnectHeader
      :subTitle="$t('home.title')"
      :showStatus="showSection"
      :userInfo="auth.myInfo"
      :urls="$url"
      @logout="$store.commit('auth/LOGOUT')"
    />
    <main>
      <nuxt />
    </main>
    <VirnectFooter />
  </div>
</template>

<script>
export default {
  middleware: ['default'],
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
  beforeMount() {
    this.$store.dispatch('auth/getAuth')
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
