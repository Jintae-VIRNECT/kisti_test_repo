<template>
  <el-card class="current-report-list el-card--table">
    <div slot="header">
      <h3>{{ $t('home.reportList.title') }}</h3>
    </div>
    <el-table :data="reports">
      <template slot="empty">
        <img src="~assets/images/empty/img-common-empty.jpg" />
        <p>{{ $t('home.reportList.empty') }}</p>
      </template>
    </el-table>
  </el-card>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      reports: [],
    }
  },
}
</script>

<style lang="scss"></style>
