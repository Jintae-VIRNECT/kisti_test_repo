<template>
  <!-- Not chrome -->
  <div v-if="error.message === 'BrowserNotSupport'" class="error-container">
    <browser-not-support />
  </div>
  <!-- 404 -->
  <div v-else-if="error.statusCode === 404" class="error-container">
    <common-error />
  </div>
  <!-- 500 -->
  <div v-else class="error-container">
    <common-error :code="error.statusCode" :message="error.message" />
  </div>
</template>

<script>
import CommonError from 'WC-Modules/vue/components/errors/CommonError'
// import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'
import BrowserNotSupport from 'WC-Modules/vue/components/errors/BrowserNotSupport'

export default {
  layout: 'noSidebar',
  components: {
    CommonError,
    BrowserNotSupport,
  },
  props: ['error'],
}
</script>

<style lang="scss">
.error-container {
  padding-top: 35px;
}
</style>
