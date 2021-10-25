<template>
  <tab-view :title="$t('workspace.select_workspace')" @refresh="refresh">
    <div class="workcard-list">
      <workspace-card
        v-for="workspaceInfo of workspaceList"
        :thumbStyle="thumbStyle"
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

const DEFAULT_THUMBSTYLE_SIZE = { width: '5.143rem', height: '5.143rem' }
const MOBILE_THUMBSTYLE_SIZE = { width: '4.8rem', height: '4.8rem' }

export default {
  components: {
    TabView,
    WorkspaceCard,
  },
  computed: {
    ...mapGetters(['workspaceList']),
  },
  data() {
    return {
      thumbStyle: {
        width: DEFAULT_THUMBSTYLE_SIZE,
        height: DEFAULT_THUMBSTYLE_SIZE,
      },
    }
  },
  watch: {
    isMobileSize: {
      immediate: true,
      handler: function(newVal) {
        if (newVal) this.thumbStyle = MOBILE_THUMBSTYLE_SIZE
        else this.thumbStyle = DEFAULT_THUMBSTYLE_SIZE
      },
    },
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
