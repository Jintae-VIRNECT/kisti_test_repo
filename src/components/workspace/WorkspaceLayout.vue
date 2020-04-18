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
  </vue2-scrollbar>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
import { getAccount } from 'api/common/account'
export default {
  name: 'WorkspaceLayout',
  components: {
    WorkspaceWelcome,
    WorkspaceTab,
  },
  data() {
    return {
      tabFix: false,
      tabTop: 0,
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
    this.updateAccount({
      userId: 123456,
      profile: require('assets/image/img_user_profile.svg'),
      description: null,
      email: 'remote@remote.com',
      name: '리모트',
      serviceInfo: null,
      userType: 'Manager',
      uuid: null,
    })
    try {
      const datas = await getAccount({
        email: 'smic1',
        password: 'smic1234',
        rememberMe: false,
      })
      console.log(datas)
    } catch (err) {
      // 에러처리
      console.error(err)
    }
  },
  mounted() {
    this.tabTop = this.$refs['tabSection'].$el.offsetTop
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
