<template>
  <div class="workspace-info">
    <div class="info">
      <img src="~assets/images/workspace-profile.png" />
      <h5>{{ info.name }}</h5>
      <span>{{ info.description || $t('workspace.descriptionEmpty') }}</span>
    </div>
    <el-divider />
    <div class="members">
      <h6>{{ $t('workspace.info') }}</h6>
      <!-- 마스터 -->
      <span>{{ $t('workspace.master') }}</span>
      <span class="count">1</span>
      <div class="users">
        <el-tooltip :content="master.name">
          <div class="avatar">
            <img :src="master.profile" />
          </div>
        </el-tooltip>
      </div>
      <!-- 매니저 -->
      <span>{{ $t('workspace.manager') }}</span>
      <span class="count">{{ managers.length }}</span>
      <div class="users">
        <el-tooltip
          v-for="user in managers"
          :key="user.uuid"
          :content="user.name"
        >
          <div class="avatar">
            <img :src="user.profile" />
          </div>
        </el-tooltip>
      </div>
      <!-- 멤버 -->
      <span>{{ $t('workspace.member') }}</span>
      <span class="count">{{ members.length }}</span>
      <div class="users">
        <el-tooltip
          v-for="user in members"
          :key="user.uuid"
          :content="user.name"
        >
          <div class="avatar">
            <img :src="user.profile" />
          </div>
        </el-tooltip>
      </div>
    </div>
    <el-divider />
    <div class="plans"></div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      info: {},
      master: {},
      managers: [],
      members: [],
    }
  },
  methods: {
    async getWorkspaceInfo() {
      const workspaceInfo = await workspaceService.getWorkspaceInfo()
      this.info = workspaceInfo.info
      this.master = workspaceInfo.master
      this.members = workspaceInfo.managers
      this.members = workspaceInfo.members
    },
  },
  beforeMount() {
    this.getWorkspaceInfo()
    workspaceService.watchActiveWorkspace(this.getWorkspaceInfo)
  },
}
</script>

<style lang="scss">
.workspace-info {
  margin: 24px;

  .info {
    margin-bottom: 40px;
    text-align: center;

    & > img {
      width: 72px;
      margin: 20px auto 12px;
    }
    & > h5 {
      font-size: 18px;
      line-height: 24px;
    }
    & > span {
      font-size: 12.6px;
      line-height: 20px;
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
</style>
