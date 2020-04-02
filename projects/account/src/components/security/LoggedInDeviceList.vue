<template>
  <div class="logged-in-device-list">
    <el-table :data="loggedInDevices">
      <column-default :label="$t('security.column.deviceName')" prop="name" />
      <column-default
        :label="$t('security.column.loginLocation')"
        prop="location"
        :width="220"
      />
      <column-default
        :label="$t('security.column.loginDate')"
        prop="loginDate"
        :width="100"
      />
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
