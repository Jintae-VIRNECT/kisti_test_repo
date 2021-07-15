<template>
  <VirnectErrorPage
    :type="error.type"
    :code="error.statusCode"
    :message="error.message"
  />
</template>

<script>
export default {
  layout: 'noSidebar',
  props: ['error'],
}
</script>

<style lang="scss">
@include responsive-to(max, 'small') {
  #__nuxt .virnect-error-page {
    padding: 24px;
  }
}
</style>
