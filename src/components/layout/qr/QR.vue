<template>
  <section>
    <Header
      v-if="$env !== 'onpremise'"
      :showStatus="showStatus"
      :userInfo="userInfo"
      :env="$env"
      :urls="$urls"
      :subTitle="$t('qrLogin.title')"
      @logout="logout"
    />
    <Header
      v-else
      :showStatus="showStatus"
      :userInfo="userInfo"
      :env="$env"
      :urls="$urls"
      :subTitle="$t('qrLogin.title')"
      :logo="logo"
      @isMobile="isMobile"
    />
    <transition name="app-fade" mode="out-in">
      <router-view
        :userInfo="userInfo"
        :customInfo="customInfo"
        :env="$env"
        :subTitle="$t('qrLogin.title')"
      />
    </transition>
    <Footer :urls="$urls" v-if="$env !== 'onpremise'" />
  </section>
</template>

<script>
import auth from '@virnect/platform-auth'
import { Header } from '@virnect/components'
import { Footer } from '@virnect/components'
export default {
  components: {
    Header,
    Footer,
  },
  props: {
    showStatus: Object,
    auth: Object,
  },
  data() {
    return {
      qrImg: null,
      userInfo: null,
      logo: {},
    }
  },
  methods: {
    async logout() {
      try {
        await auth.logout()
        location.reload()
      } catch (e) {
        console.error(e)
      }
    },
  },
  watch: {
    customInfo() {
      this.logo = {
        default: this.customInfo.default,
        white: this.customInfo.white,
      }
    },
  },
  computed: {
    customInfo() {
      return this.$store.getters.customInfo
    },
  },
  async mounted() {
    try {
      if (auth.isLogin) {
        this.userInfo = auth.myInfo
        this.showStatus.login = !auth.isLogin
        this.showStatus.profile = true
        this.showStatus.language = false
      } else throw 'error'
    } catch (e) {
      this.showStatus.profile = false
      location.replace(`${this.$urls['console']}/?continue=${location.href}`)
    }
  },
}
</script>
