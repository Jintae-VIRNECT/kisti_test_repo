<template>
  <div>
    <header>
      <the-header :logoImg="logoImg" :showSection="showSection" :auth="auth">
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
    <!-- jennifer test -->
    <script v-if="$config.VIRNECT_ENV === 'develop'">
      ;(function(j, en, ni, fer) {
        j['dmndata'] = []
        j['jenniferFront'] = function(args) {
          window.dmndata.push(args)
        }
        j['dmnaid'] = fer
        j['dmnatime'] = new Date()
        j['dmnanocookie'] = false
        j['dmnajennifer'] = 'JENNIFER_FRONT@INTG'
        var b = Math.floor(new Date().getTime() / 60000) * 60000
        var a = en.createElement(ni)
        a.src = 'https://d-collect.jennifersoft.com/' + fer + '/demian.js?' + b
        a.async = true
        en.getElementsByTagName(ni)[0].parentNode.appendChild(a)
      })(window, document, 'script', '26cceb07')
    </script>
  </div>
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideMenus, sideBottomMenus } from '@/models/layout'
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  components: {
    TheSidebar,
    TheHeader,
  },
  head() {
    return {
      title: `${this.title} | ${this.$t('common.workstation')}`,
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
  mounted() {
    this.$store.dispatch('auth/getAuth')

    // 콘솔 표시
    console.log(
      `%cVirnect Workstation v${this.$config.VERSION}`,
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

    // 사파리 테이블 버그
    document
      .querySelectorAll('.el-table__body')
      .forEach(table => (table.style.tableLayout = 'auto'))
    setTimeout(() => {
      document
        .querySelectorAll('.el-table__body')
        .forEach(table => (table.style.tableLayout = 'fixed'))
    }, 10)
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
