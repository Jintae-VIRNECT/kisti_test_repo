<template>
  <div>
    <header>
      <the-header :showSection="showSection">
        <template slot="subTitle">{{ $t('menu.account') }}</template>
      </the-header>
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
