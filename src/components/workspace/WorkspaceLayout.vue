<template>
  <div class="remote-wrapper workspace-wrapper">
    <workspace-welcome></workspace-welcome>
    <workspace-tab></workspace-tab>
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
    return {}
  },
  computed: {
    ...mapGetters(['account']),
  },
  watch: {},
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
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
