<template>
  <div class="logged-in-device-list">
    <el-table ref="table" :data="accessLogs">
      <ColumnDefault
        :label="$t('security.column.deviceName')"
        prop="device"
        :dangerouslyUseHTMLString="true"
      />
      <ColumnDefault
        :label="$t('security.column.loginLocation')"
        prop="location"
        :width="200"
      />
      <ColumnDate
        :label="$t('security.column.loginDate')"
        prop="loginDate"
        type="time"
        :width="160"
      />
    </el-table>
    <SearchbarPage
      v-if="!isHome"
      ref="page"
      :value.sync="accessLogsPage"
      :total="accessLogsTotal"
    />
  </div>
</template>

<script>
import profileService from '@/services/profile'
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'

export default {
  mixins: [columnMixin, searchMixin],
  props: {
    isHome: Boolean,
  },
  data() {
    return {
      accessLogs: [],
      accessLogsTotal: 0,
      accessLogsPage: 1,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.getLoggedInDeviceList(searchParams)
    },
    async getLoggedInDeviceList(searchParams) {
      const { list, total } = await profileService.getLoggedInDeviceList(
        searchParams,
      )
      this.accessLogs = list
      this.accessLogsTotal = total
      // 현재기기 텍스트 삽입
      if (this.accessLogs[0] && this.accessLogsPage === 1) {
        this.accessLogs[0].device += `
        <span class="now">
          ${this.$t('security.now')}
        </span>`
      }
    },
  },
  beforeMount() {
    this.getLoggedInDeviceList({
      size: this.isHome ? 5 : 10,
    })
  },
}
</script>

<style lang="scss">
.logged-in-device-list {
  .now {
    margin-left: 24px;
    color: #34aa44;
    &:before {
      font-weight: bold;
      content: '·';
    }
  }
}
</style>
