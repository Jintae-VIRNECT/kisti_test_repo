<template>
  <div>
    <VirnectHeader
      :env="$env"
      :subTitle="$t('menu.account')"
      :showStatus="showSection"
      :userInfo="myProfile"
      :urls="$url"
      :logo="{ default: logo }"
      @logout="$store.commit('auth/LOGOUT')"
    />
    <TheSidebar :menus="sideMenus" />
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
import { sideMenus, sideMenus_op } from '@/models/layout'
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  head() {
    return {
      title: `${this.title} | ${this.$t('menu.account')}`,
      htmlAttrs: {
        lang: this.$i18n.locale,
        ontouchmove: '',
      },
      meta: [
        { name: 'viewport', content: 'width=device-width, user-scalable=no' },
      ],
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
      sideMenus: this.$isOnpremise ? sideMenus_op : sideMenus,
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
      myProfile: 'auth/myProfile',
      title: 'layout/title',
      logo: 'layout/logo',
      favicon: 'layout/favicon',
    }),
  },
  mounted() {
    this.$store.dispatch('auth/getAuth')

    // 콘솔 표시
    console.log(
      `%cVirnect Account v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.VIRNECT_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)

    // 언어 선택 쿼리
    const lang = this.$route.query.lang
    if (this.$i18n.locales.includes(lang)) {
      this.$store.dispatch('CHANGE_LANG', lang)
      this.$i18n.locale = lang
    }
  },
}
</script>

<style lang="scss">
#headerSection .sub-title {
  font-size: 18px;
}
</style>
