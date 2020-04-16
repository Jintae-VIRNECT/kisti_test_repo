<template>
  <div v-if="(error.statusCode + '')[0] === '4'">
    <page-not-found :code="error.statusCode" link="/" />
  </div>
  <div v-else-if="(error.statusCode + '')[0] === '5'">
    <connection-error :code="error.statusCode" link="/" />
  </div>
</template>

<script>
import PageNotFound from 'WC-Modules/vue/components/errors/404'
import ConnectionError from 'WC-Modules/vue/components/errors/504'

export default {
  layout: 'noSidebar',
  components: {
    PageNotFound,
    ConnectionError,
  },
  props: ['error'],
}
</script>
