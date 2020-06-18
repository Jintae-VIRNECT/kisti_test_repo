<template>
  <div id="workspace">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.workspace') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.workspace') }}</h2>
        <p>{{ $t('workspace.desc') }}</p>
      </div>
      <!-- 워크스페이스 리스트 -->
      <workspace-list />
      <!-- 플랜 리스트 -->
      <using-plan-list />
    </div>
  </div>
</template>

<script>
import WorkspaceList from '@/components/workspace/WorkspaceList'
import UsingPlanList from '@/components/workspace/UsingPlanList'

export default {
  components: {
    WorkspaceList,
    UsingPlanList,
  },
}
</script>

<style lang="scss">
#__nuxt #workspace {
  .el-table__body-wrapper {
    min-height: 384px;
  }
}
</style>
