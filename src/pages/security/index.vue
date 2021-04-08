<template>
  <div id="security">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.security') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.security') }}</h2>
        <p>{{ $t('security.desc') }}</p>
      </div>
      <el-card class="el-card--table">
        <div slot="header">
          <h3>{{ $t('security.loggedInDevice') }}</h3>
          <span>{{ $t('security.loggedInDeviceDesc') }}</span>
          <button @click="SecurityEnhanceVisible = true">
            {{ $t('security.notMyDevice') }}
          </button>
        </div>
        <SecurityLoggedInDeviceList />
      </el-card>

      <SecurityEnhanceModal :visible.sync="SecurityEnhanceVisible" />
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      SecurityEnhanceVisible: false,
    }
  },
}
</script>
