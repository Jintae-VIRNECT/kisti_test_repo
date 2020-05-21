<template>
  <div class="workspace-info" v-loading="loading">
    <div class="info">
      <div class="avatar">
        <div
          class="image"
          :style="`background-image: url(${workspaceInfo.info.profile})`"
        />
      </div>
      <h5>{{ workspaceInfo.info.name }}</h5>
      <span>{{
        workspaceInfo.info.description || $t('workspace.info.descriptionEmpty')
      }}</span>
    </div>
    <el-divider />
    <div class="members">
      <h6>{{ $t('workspace.info.title') }}</h6>
      <!-- 마스터 -->
      <span>{{ $t('workspace.master') }}</span>
      <span class="count">1</span>
      <div class="users">
        <el-tooltip :content="workspaceInfo.master.name">
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${workspaceInfo.master.profile})`"
            />
          </div>
        </el-tooltip>
      </div>
      <!-- 매니저 -->
      <span>{{ $t('workspace.manager') }}</span>
      <span class="count">{{ workspaceInfo.managers.length }}</span>
      <div class="users">
        <el-tooltip
          v-for="user in workspaceInfo.managers"
          :key="user.uuid"
          :content="user.name"
        >
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${user.profile})`"
            />
          </div>
        </el-tooltip>
      </div>
      <!-- 멤버 -->
      <span>{{ $t('workspace.member') }}</span>
      <span class="count">{{ workspaceInfo.members.length }}</span>
    </div>
    <el-divider />
    <div class="plans"></div>
    <el-button @click="addMember">
      {{ $t('workspace.info.addMember') }}
    </el-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'

export default {
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
  data() {
    return {
      loading: false,
      workspaceInfo: {
        info: {},
        master: [],
        managers: [],
        members: [],
      },
    }
  },
  methods: {
    addMember() {
      this.$router.push({
        path: '/members',
        query: { modal: 'memberAdd' },
      })
    },
    async getWorkspaceInfo() {
      this.loading = true
      this.workspaceInfo = await workspaceService.getWorkspaceInfo(
        this.activeWorkspace.uuid,
      )
      this.loading = false
    },
  },
  async beforeMount() {
    this.getWorkspaceInfo()
    workspaceService.watchActiveWorkspace(this, this.getWorkspaceInfo)
  },
}
</script>

<style lang="scss">
.workspace-info {
  margin: 24px;

  .info {
    margin-bottom: 40px;
    text-align: center;

    & > .avatar {
      width: 72px;
      height: 72px;
      margin: 40px auto 12px;
    }
    & > h5 {
      font-size: 18px;
      line-height: 24px;
    }
    & > span {
      font-size: 12.6px;
      line-height: 20px;
      opacity: 0.75;
    }
  }
  .members {
    & > h6 {
      margin-bottom: 16px;
      font-size: 13.6px;
      line-height: 20px;
    }
    & > span {
      color: $font-color-desc;
      font-size: 12px;
    }
    & > .count {
      float: right;
      color: $font-color-content;
      font-size: 14px;
    }
    .users {
      margin: 12px 0;
      .avatar {
        display: inline-block;
        width: 28px;
        height: 28px;
        margin-right: 4px;
      }
    }
  }
}
#__nuxt .workspace-info .el-button {
  width: 100%;
  padding: 12px;
}
</style>
