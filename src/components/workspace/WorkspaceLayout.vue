<template>
  <vue2-scrollbar
    classes="remote-wrapper"
    ref="wrapperScroller"
    @onScroll="onScroll"
  >
    <div class="workspace-wrapper">
      <workspace-welcome ref="welcomeSection"></workspace-welcome>
      <workspace-tab
        ref="tabSection"
        :fix="tabFix"
        @tabChange="tabChange"
      ></workspace-tab>
    </div>
    <cookie-policy v-if="showCookie" :visible.sync="showCookie"></cookie-policy>
  </vue2-scrollbar>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
// import { getAccount } from 'api/common/account'
import auth from 'utils/auth'

export default {
  name: 'WorkspaceLayout',
  async beforeMount() {
    const account = await auth.init()
    this.updateAccount(account.myInfo)
    if (!auth.isLogin) {
      auth.login()
    }
  },
  components: {
    WorkspaceWelcome,
    WorkspaceTab,
    CookiePolicy: () => import('CookiePolicy'),
  },
  data() {
    const cookie = Number.parseInt(
      localStorage.getItem('ServiceCookiesAgree'),
      10,
    )
    return {
      tabFix: false,
      tabTop: 0,
      showCookie: !cookie,
    }
  },
  computed: {
    ...mapGetters(['account']),
  },
  methods: {
    ...mapActions(['updateAccount']),
    onScroll(scrollX, scrollY) {
      if (scrollY > this.tabTop) {
        this.tabFix = true
      } else {
        this.tabFix = false
      }
    },
    tabChange() {
      this.$refs['wrapperScroller'].scrollToY(0)
      this.tabFix = false
    },
  },

  /* Lifecycles */
  async created() {
    // this.updateAccount({
    //   userId: '123456',
    //   profile: require('assets/image/profile.png'),
    //   description: null,
    //   email: 'remote@remote.com',
    //   name: '리모트',
    //   serviceInfo: null,
    //   userType: 'Manager',
    //   uuid: null,
    // })
    // try {
    //   const datas = await getAccount()
    //   console.log(datas)
    // } catch (err) {
    //   // 에러처리
    //   console.error(err)
    // }
  },
  mounted() {
    this.tabTop = this.$refs['tabSection'].$el.offsetTop
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
