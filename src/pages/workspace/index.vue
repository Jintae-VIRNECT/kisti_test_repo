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
      <el-card class="el-card--table">
        <div slot="header">
          <h3>{{ $t('workspace.list.title') }}</h3>
        </div>
        <WorkspaceList />
      </el-card>
      <!-- 플랜 리스트 -->
      <el-card class="el-card--table">
        <div slot="header">
          <h3>{{ $t('workspace.usingPlanList.title') }}</h3>
        </div>
        <WorkspaceUsingPlanList />
      </el-card>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss">
#__nuxt #workspace {
  .el-table__body-wrapper {
    min-height: 384px;
  }
}
</style>
