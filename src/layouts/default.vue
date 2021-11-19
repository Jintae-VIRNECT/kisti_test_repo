<template>
  <div :class="{ onpremise: $isOnpremise }">
    <VirnectHeader
      :env="$env"
      :showStatus="showSection"
      :userInfo="auth.myInfo"
      :urls="$url"
      :logo="{ default: logo }"
      @logout="$store.commit('auth/LOGOUT')"
    >
      <template slot="subTitle">
        <VirnectWorkspaceSelect
          :activeWorkspace="activeWorkspace"
          :workspaceList="myWorkspaces"
          :newWorkspace="false"
          @onChange="changeActiveWorkspace"
          @click.native.prevent
          @addNewWorkspaceButton="$router.push({ path: '/start' })"
        />
      </template>
    </VirnectHeader>
    <div>
      <LayoutTheSidebar :menus="menus" :bottomMenus="bottomMenus" />
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import {
  sideMenus,
  sideBottomMenus,
  sideOnpremiseBottomMenus,
} from '@/models/layout'
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  head() {
    return {
      title: `${this.title} | ${this.$t('common.workstation')}`,
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
      showSection: {
        profile: true,
        portal: true,
      },
      menus: sideMenus,
      bottomMenus: this.$isOnpremise
        ? sideOnpremiseBottomMenus
        : sideBottomMenus,
    }
  },
  computed: {
    ...mapGetters({
      auth: 'auth/auth',
      myWorkspaces: 'auth/myWorkspaces',
      activeWorkspace: 'auth/activeWorkspace',
      title: 'layout/title',
      logo: 'layout/logo',
      favicon: 'layout/favicon',
    }),
  },
  methods: {
    changeActiveWorkspace(workspace) {
      this.$store.commit('auth/SET_ACTIVE_WORKSPACE', workspace.uuid)
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
  },
}
</script>

<style lang="scss">
:not(.no-sidebar) > .header-section {
  left: $the-sidebar-width !important;
  width: calc(100% - #{$the-sidebar-width}) !important;
}
#headerSection .virnect-workspace-select {
  margin-left: 20px;
}
// onpremise 환경에서 워크스테이션 선택기능 비활성화
#__nuxt .onpremise .virnect-workspace-select {
  pointer-events: none;
  &__value {
    background: none;
    border: none;
    &:after {
      display: none;
    }
  }
}
</style>
