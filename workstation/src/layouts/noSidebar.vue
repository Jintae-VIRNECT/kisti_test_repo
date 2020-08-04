<template>
  <div>
    <header class="no-sidebar">
      <the-header :showSection="showSection" />
    </header>
    <div>
      <main>
        <nuxt />
      </main>
    </div>
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
      title: `VIRNECT | ${this.$t('common.workstation')}`,
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
      `%cVirnect Workstation v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.TARGET_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
  },
}
</script>
