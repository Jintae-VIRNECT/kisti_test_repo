<template>
  <div class="the-sidebar__collapse--workspace">
    <div class="the-sidebar__collapse__header">
      <h5>{{ $t('menu.collapse.workspace.title') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <span>{{ $t('menu.collapse.workspace.master') }}</span>
      <button
        v-for="workspace in workspaces.master"
        :key="workspace.uuid"
        @click="activeWorkspace(workspace.uuid)"
      >
        <span>{{ workspace.uuid }}</span>
      </button>
      <span>{{ $t('menu.collapse.workspace.manager') }}</span>
      <button
        v-for="workspace in workspaces.manager"
        :key="workspace.uuid"
        @click="activeWorkspace(workspace.uuid)"
      >
        <span>{{ workspace.uuid }}</span>
      </button>
      <span>{{ $t('menu.collapse.workspace.member') }}</span>
      <button
        v-for="workspace in workspaces.member"
        :key="workspace.uuid"
        @click="activeWorkspace(workspace.uuid)"
      >
        <span>{{ workspace.uuid }}</span>
      </button>
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      workspaces: {
        master: [],
        manager: [],
        member: [],
      },
    }
  },
  methods: {
    activeWorkspace(uuid) {
      this.$store.commit('workspace/SET_ACTIVE_WORKSPACE', uuid)
      this.$emit('closeCollapse')
    },
  },
  async beforeCreate() {
    this.workspaces = await workspaceService.getMyWorkspaces()
  },
}
</script>

<style lang="scss">
.the-sidebar__collapse--workspace {
  .the-sidebar__collapse__body {
    & > span {
      display: block;
      font-size: 12px;
      opacity: 0.6;
    }
    & > button {
      display: block;
      width: 100%;
      height: 38px;
      overflow: hidden;
      color: #fff;
    }
  }
}
</style>
