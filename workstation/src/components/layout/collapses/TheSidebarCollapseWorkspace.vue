<template>
  <div class="the-sidebar__collapse--workspace">
    <div class="the-sidebar__collapse__header">
      <h5>{{ $t('menu.collapse.workspace.title') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <div v-for="(group, role) in workspaces" :key="role">
        <span>{{ $t(`menu.collapse.workspace.${role}`) }}</span>
        <button
          v-for="workspace in workspaces[role]"
          :class="isActive(workspace.uuid) ? 'selected' : ''"
          :key="workspace.uuid"
          @click="workspaceActive(workspace.uuid)"
        >
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${workspace.profile})`"
            />
          </div>
          <span>{{ workspace.name }}</span>
          <img
            v-if="isActive(workspace.uuid)"
            src="~assets/images/icon/ic-check-circle.svg"
          />
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
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
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
  methods: {
    async workspaceActive(uuid) {
      await this.$store.dispatch('workspace/getActiveWorkspaceInfo', {
        route: {
          workspaceId: uuid,
        },
      })
      this.$emit('closeCollapse')
    },
    isActive(uuid) {
      return this.activeWorkspace.info.uuid === uuid
    },
  },
  async beforeCreate() {
    this.workspaces = await workspaceService.getMyWorkspaces()
  },
}
</script>

<style lang="scss">
.the-sidebar__collapse--workspace .the-sidebar__collapse__body > div {
  & > span {
    display: block;
    margin: 8px 0;
    font-size: 12px;
    opacity: 0.6;
  }
  & > button {
    display: block;
    width: 100%;
    height: 38px;
    margin: 4px 0;
    overflow: hidden;
    color: #fff;
    text-align: left;
    border-radius: 4px;

    .avatar {
      display: inline-block;
      width: 24px;
      height: 24px;
      margin: 7px 8px;
      vertical-align: middle;
    }
    & > span {
      display: inline-block;
      width: 144px;
      overflow: hidden;
      line-height: 1.5;
      white-space: nowrap;
      text-overflow: ellipsis;
      vertical-align: middle;
    }
    & > img {
      float: right;
      margin: 11px 9px 11px 0;
    }

    &:hover {
      background: rgba(44, 62, 93, 0.6);
    }
    &:active,
    &.selected {
      background: #324461;
    }
  }
}
</style>
