<template>
  <div class="workspace-info">
    <div class="title">
      <img src="~assets/images/workspace-profile.png" />
      <h5></h5>
      <span></span>
    </div>
    <el-divider />
    <div class="info">
      {{ info }}
    </div>
    <el-divider />
    <div class="members">
      {{ members }}
    </div>
    <div class="plans">
      {{ plansCount }}
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      info: {},
      members: [],
      plansCount: {},
    }
  },
  async beforeCreate() {
    const workspaceInfo = await workspaceService.getWorkspaceInfo(
      '4d6eab0860969a50acbfa4599fbb5ae8',
    )
    this.info = workspaceInfo.info
    this.members = workspaceInfo.members
    this.plansCount = workspaceInfo.plansCount
  },
}
</script>

<style lang="scss">
.workspace-info {
  margin: 24px 0;

  .title {
    text-align: center;
    & > img {
      width: 72px;
    }
  }
}
</style>
