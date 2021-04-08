<template>
  <main>
    <nuxt />
  </main>
</template>

<script>
export default {
  mounted() {
    // 콘솔 표시
    console.log(
      `%cVirnect Download v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.VIRNECT_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
  },
}
</script>
