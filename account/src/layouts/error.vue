<template>
  <div v-if="error.statusCode === 404">
    <page-not-found />
  </div>
</template>

<script>
import PageNotFound from 'WC-Modules/vue/components/errors/404'
export default {
  layout: 'noSidebar',
  components: {
    PageNotFound,
  },
  props: ['error'],
}
</script>
