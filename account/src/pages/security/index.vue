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
        <logged-in-device-list />
      </el-card>

      <security-enhance-modal :visible.sync="SecurityEnhanceVisible" />
    </div>
  </div>
</template>

<script>
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'
import SecurityEnhanceModal from '@/components/security/SecurityEnhanceModal'

export default {
  components: {
    LoggedInDeviceList,
    SecurityEnhanceModal,
  },
  data() {
    return {
      SecurityEnhanceVisible: false,
    }
  },
}
</script>
