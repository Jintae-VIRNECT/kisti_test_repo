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
import thumbStyle from 'mixins/thumbStyle'

const DEFAULT_THUMBSTYLE_SIZE = '5.143rem'
const MOBILE_THUMBSTYLE_SIZE = '4.8rem'

export default {
  components: {
    TabView,
    WorkspaceCard,
  },
  mixins: [thumbStyle],
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
  methods: {
    ...mapActions(['changeWorkspace']),
    refresh() {},
    join(workspace) {
      this.$eventBus.$emit('scroll:reset:workspace')
      this.changeWorkspace(workspace)
    },
  },
  mounted() {
    this.setSizeVariable(
      DEFAULT_THUMBSTYLE_SIZE,
      DEFAULT_THUMBSTYLE_SIZE,
      MOBILE_THUMBSTYLE_SIZE,
      MOBILE_THUMBSTYLE_SIZE,
    )
    this.activateThumbStyleHandlerOnMobileSize()
  },
}
</script>

<style></style>
