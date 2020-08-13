<template>
  <tab-view :title="$t('workspace.select_workspace')" @refresh="refresh">
    <div class="workcard-list">
      <workspace-card
        v-for="workspaceInfo of workspaceList"
        :key="'workspace_' + workspaceInfo.uuid"
        :workspaceInfo="workspaceInfo"
        @join="join(workspaceInfo)"
      ></workspace-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceCard from 'WorkspaceCard'
import { mapGetters, mapActions } from 'vuex'
export default {
  components: {
    TabView,
    WorkspaceCard,
  },
  computed: {
    ...mapGetters(['workspaceList']),
  },
  methods: {
    ...mapActions(['changeWorkspace']),
    refresh() {},
    join(workspace) {
      this.$eventBus.$emit('scroll:reset:workspace')
      this.changeWorkspace(workspace)
    },
  },
}
</script>

<style></style>
