<template>
  <section class="workspace-setting-section">
    <div>
      <div class="workspace-setting-title">
        알림 설정
      </div>
      <div class="workspace-setting-sub-title">PC 알림 활성화</div>
      <div class="workspace-setting-horizon-wrapper">
        <div class="workspace-setting-label">
          상대방과 통화 연결 및 연격제어 중에 알림을 받고 싶으시다면 알림설정을
          On으로 해주세요.
        </div>
        <div>
          <el-switch v-model="getPcNotiWhileService"> </el-switch>
        </div>
      </div>
    </div>
  </section>
</template>
<script>
export default {
  data: function() {
    return {
      getPcNotiWhileService: true,
    }
  },
}
</script>

<style scoped></style>
