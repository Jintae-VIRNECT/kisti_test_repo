<template>
  <div class="logged-in-device-list">
    <el-table :data="loggedInDevices">
      <column-default label="기기 이름" prop="name" />
      <column-default label="최종 접속 위치" prop="location" :width="220" />
      <column-default label="최종 접속일" prop="loginDate" :width="100" />
    </el-table>
  </div>
</template>

<script>
import securityService from '@/services/security'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'

export default {
  components: {
    ColumnDefault,
  },
  data() {
    return {
      loggedInDevices: securityService.getLoggedInDeviceList(),
    }
  },
}
</script>
