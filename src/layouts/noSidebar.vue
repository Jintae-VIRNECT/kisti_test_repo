<template>
  <div class="no-sidebar">
    <VirnectHeader
      :env="$env"
      :subTitle="$t('menu.account')"
      :showStatus="showSection"
      :userInfo="auth.myInfo"
      :urls="$url"
      :logo="{ default: logo }"
      @logout="$store.commit('auth/LOGOUT')"
    />
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  head() {
    return {
      title: `${this.title} | ${this.$t('menu.account')}`,
      htmlAttrs: {
        lang: this.$i18n.locale,
      },
      link: [
        {
          rel: 'icon',
          type: 'image/x-icon',
          href: this.favicon,
        },
      ],
    }
  },
  data() {
    return {
      showSection: {
        login: false,
        lang: false,
        link: true,
        profile: true,
      },
    }
  },
  computed: {
    ...mapGetters({
      auth: 'auth/auth',
      title: 'layout/title',
      logo: 'layout/logo',
      favicon: 'layout/favicon',
    }),
    logoImg() {
      return {
        default: this.logo,
        x2: this.logo,
        x3: this.logo,
      }
    },
  },
  beforeMount() {
    // 콘솔 표시
    console.log(
      `%cVirnect Account v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.VIRNECT_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)
    this.$store.dispatch('auth/getAuth')
  },
}
</script>

<style lang="scss">
.no-sidebar {
  main {
    min-width: 0;
  }
  .container {
    width: auto;
  }
}
</style>
