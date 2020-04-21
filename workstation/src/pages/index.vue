<template>
  <div id="home">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item to="/">{{ $t('menu.home') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.home') }}</h2>
      </div>
      <el-row>
        <el-col class="container__left">
          <el-row>
            <el-card class="el-card--table">
              <workspace-info />
            </el-card>
          </el-row>
          <el-row>
            <el-card></el-card>
          </el-row>
        </el-col>
        <el-col class="container__center"> <el-card></el-card></el-col>
        <el-col class="container__right"> <el-card></el-card></el-col>
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
  data() {
    return {
      nickname: `User's Nickname`,
      form: {
        name: '',
        description: '',
      },
    }
  },
}
</script>

<style lang="scss">
#home .container {
  width: 1752px;
}
</style>
