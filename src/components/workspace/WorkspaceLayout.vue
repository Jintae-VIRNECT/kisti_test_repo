<template>
  <div class="remote-wrapper workspace-wrapper">
    <workspace-welcome></workspace-welcome>
    <workspace-tab></workspace-tab>
  </div>
</template>

<script>
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
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
