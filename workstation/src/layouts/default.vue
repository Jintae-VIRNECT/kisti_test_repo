<template>
  <div>
    <header>
      <the-header :showSection="showSection" :auth="auth">
        <template slot="subTitle">
          <el-divider direction="vertical" />
          <div class="avatar">
            <div
              class="image"
              :style="
                `background-image: url(${activeWorkspace.profile}), url(${$defaultWorkspaceProfile})`
              "
            />
          </div>
          <span>{{ activeWorkspace.name }}</span>
        </template>
      </the-header>
    </header>
    <div>
      <the-sidebar :menus="sideMenus" :bottomMenus="sideBottomMenus" />
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideMenus, sideBottomMenus } from '@/models/layout'
import { mapGetters } from 'vuex'

import Cookies from 'js-cookie'

export default {
  middleware: 'default',
  components: {
    TheSidebar,
    TheHeader,
  },
  head() {
    return {
      title: `VIRNECT | ${this.$t('common.workstation')}`,
      htmlAttrs: {
        lang: this.$i18n.locale,
      },
    }
  },
  data() {
    return {
      showSection: {
        profile: true,
        link: true,
      },
      sideMenus,
      sideBottomMenus,
    }
  },
  computed: {
    ...mapGetters({
      auth: 'auth/auth',
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  mounted() {
    this.$store.dispatch('auth/getAuth', this.$config.TARGET_ENV)

    // 콘솔 표시
    console.log(
      `%cVirnect Workstation v${this.$config.VERSION}`,
      'font-size: 20px; color: #1468e2',
    )
    console.log(`env: ${this.$config.TARGET_ENV}`)
    console.log(`timeout: ${this.$config.API_TIMEOUT}`)

    // 기본 언어 쿠키
    if (!Cookies.get('lang')) {
      Cookies.set('lang', this.$i18n.locale, {
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
      })
    }

    // 서버 메세지 푸시
    const message = this.$route.query.message
    if (message) {
      this.$message.error({
        message: this.$t(message),
        showClose: true,
        duration: 0,
      })
      this.$router.replace(this.$route.path)
    }
  },
}
</script>

<style lang="scss">
:not(.no-sidebar) > .header-section {
  left: $the-sidebar-width !important;
  width: calc(100% - #{$the-sidebar-width}) !important;
}
#__nuxt .sub-title {
  font-size: 14px;
  line-height: 32px;
  .el-divider {
    height: 24px;
    margin-right: 16px;
  }
  .avatar {
    display: inline-block;
    width: 22px;
    height: 22px;
    margin-right: 4px;
    vertical-align: middle;
  }
  span {
    display: inline-block;
    margin-bottom: 2px;
    vertical-align: middle;
  }
}
</style>
