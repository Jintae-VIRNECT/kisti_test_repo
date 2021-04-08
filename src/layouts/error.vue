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
.virnect-error-page {
  padding-top: 35px;
}
</style>
