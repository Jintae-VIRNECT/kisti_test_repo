<template>
  <!-- Not chrome -->
  <div v-if="error.message === 'BrowserNotSupport'" class="error-container">
    <browser-not-support />
  </div>
  <!-- 404 -->
  <div v-else-if="error.statusCode === 404" class="error-container">
    <common-error />
  </div>
  <!-- 504 -->
  <div v-else-if="error.statusCode === 504" class="error-container">
    <network-error :code="error.statusCode" :message="error.message" />
  </div>
  <!-- 500 -->
  <div v-else class="error-container">
    <common-error :code="error.statusCode" :message="error.message" />
  </div>
</template>

<script>
import CommonError from 'WC-Modules/vue/components/errors/CommonError'
import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'
import BrowserNotSupport from 'WC-Modules/vue/components/errors/BrowserNotSupport'

export default {
  layout: 'noSidebar',
  components: {
    CommonError,
    BrowserNotSupport,
    NetworkError,
  },
  props: ['error'],
  head() {
    return {
      title: `VIRNECT | ${this.$t('menu.account')}`,
    }
  },
  mounted() {
    // 콘솔 표시
    console.log(
      `%cVirnect Account v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.TARGET_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
  },
}
</script>

<style lang="scss">
.error-container {
  padding-top: 35px;
}
</style>
