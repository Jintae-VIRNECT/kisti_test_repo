<template>
  <div>
    <the-header :showSection="showSection">
      <template slot="subTitle">
        {{ $t('home.title') }}
      </template>
    </the-header>
    <!-- Not chrome -->
    <div v-if="error.message === 'BrowserNotSupport'" class="error-container">
      <browser-not-support />
    </div>
    <!-- 404 -->
    <div v-else-if="false" class="error-container">
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
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import CommonError from 'WC-Modules/vue/components/errors/CommonError'
import NetworkError from 'WC-Modules/vue/components/errors/NetworkError'
import BrowserNotSupport from 'WC-Modules/vue/components/errors/BrowserNotSupport'

export default {
  layout: 'empty',
  components: {
    TheHeader,
    CommonError,
    BrowserNotSupport,
    NetworkError,
  },
  props: ['error'],
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
    }
  },
}
</script>

<style lang="scss">
.error-container {
  padding-top: 35px;
}
@media (max-width: $mobile) {
  .error-container {
    padding-top: 0;
  }
}
</style>
