<template>
  <div>
    <the-header :showSection="showSection">
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
  mounted() {
    // 콘솔 표시
    console.log(
      `%cVirnect Download v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.TARGET_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
  },
}
</script>

<style lang="scss">
body .sub-title {
  font-size: 18px;
}
main {
  margin-top: 64px;
}
</style>
