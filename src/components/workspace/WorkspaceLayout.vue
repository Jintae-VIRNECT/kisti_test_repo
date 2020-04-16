<template>
  <!-- <vue2-scrollbar classes="remote-wrapper" ref="scroller" @onScroll="onScroll">
  </vue2-scrollbar> -->
  <div class="workspace-wrapper">
    <workspace-welcome
      :class="{ 'welcome-hide': !showWelcome }"
    ></workspace-welcome>
    <workspace-tab
      ref="workspaceTab"
      :canScroll="scroll"
      :class="{ 'welcome-hide': !showWelcome }"
    ></workspace-tab>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
export default {
  name: 'WorkspaceLayout',
  components: {
    WorkspaceWelcome,
    WorkspaceTab,
  },
  data() {
    return {
      showWelcome: true,
      scroll: false,
    }
  },
  computed: {
    ...mapGetters(['account']),
  },
  watch: {
    showWelcome(val) {
      if (val) {
        this.scroll = false
      } else {
        setTimeout(() => {
          if (!this.showWelcome) {
            this.scroll = true
          }
        }, 500)
      }
    },
  },
  methods: {
    ...mapActions(['updateAccount']),
  },

  /* Lifecycles */
  created() {
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
  },
  mounted() {
    window.addEventListener('wheel', e => {
      if (e.deltaY > 0) {
        this.showWelcome = false
      } else {
        this.showWelcome = true
      }
    })
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>

<style>
.workspace-welcome {
  transform: translateY(0);
  transition: transform 0.5s;
}
.workspace-welcome.welcome-hide {
  transform: translateY(-280px);
}
.workspace-tab {
  transform: translateY(0);
  transition: transform 0.5s;
}
.workspace-tab.welcome-hide {
  transform: translateY(-280px);
}
</style>
