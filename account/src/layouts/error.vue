<template>
  <div v-if="(error.statusCode + '')[0] === '4'">
    <page-not-found :code="error.statusCode" link="/" />
  </div>
  <div v-else-if="(error.statusCode + '')[0] === '5'">
    <connection-error :code="error.statusCode" link="/" />
  </div>
</template>

<script>
import PageNotFound from 'WC-Modules/vue/components/errors/CommonError'
import ConnectionError from 'WC-Modules/vue/components/errors/NetworkError'

export default {
  layout: 'noSidebar',
  components: {
    PageNotFound,
    ConnectionError,
  },
  props: ['error'],
}
</script>
