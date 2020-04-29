<template>
  <div>
    <the-header :showSection="showSection">
      <template slot="subTitle">
        {{ $t('home.title') }}
      </template>
    </the-header>
    <main>
      <nuxt />
    </main>
    <the-footer />
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheFooter from 'WC-Modules/vue/components/footer/TheFooter'

export default {
  middleware: ['default'],
  components: {
    TheHeader,
    TheFooter,
  },
  data() {
    return {
      showSection: {
        profile: true,
      },
    }
  },
}
</script>

<style lang="scss">
body .sub-title {
  font-size: 18px;
}
main {
  margin-top: 64px;
}
</style>
