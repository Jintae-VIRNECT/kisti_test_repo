<template>
  <div>
    <header>
      <the-header :subTitle="$t('menu.account')" :showSection="showSection" />
    </header>
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

export default {
  middleware: 'default',
  components: {
    TheHeader,
  },
  data: function() {
    return {
      showSection: {
        login: false,
        lang: false,
        link: true,
        profile: true,
      },
    }
  },
}
</script>
