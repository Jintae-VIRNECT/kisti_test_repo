<template>
  <div id="security">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>계정 관리</el-breadcrumb-item>
          <el-breadcrumb-item>보안 설정</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>보안 설정</h2>
        <p>사용자 계정의 보안을 설정합니다.</p>
      </div>
      <search-tab-nav placeholder="워크스페이스 이름 검색" />
      <el-card class="el-card--table">
        <div slot="header">
          <h3>로그인된 기기 정보</h3>
          <span>내 계정으로 로그인된 디바이스 정보입니다.</span>
        </div>
        <logged-in-device-list />
      </el-card>
    </div>
  </div>
</template>

<script>
import LoggedInDeviceList from '@/components/security/LoggedInDeviceList'

export default {
  components: {
    LoggedInDeviceList,
  },
}
</script>
