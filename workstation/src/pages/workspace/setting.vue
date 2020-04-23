<template>
  <div class="workspace-setting">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/">{{
            $t('menu.setting')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.setting') }}</h2>
      </div>
      <!-- 왼쪽 -->
      <el-row>
        <el-col class="container__left">
          <el-card class="el-card--table">
            <workspace-info />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'

export default {
  components: {
    WorkspaceInfo,
  },
}
</script>

<style lang="scss"></style>
