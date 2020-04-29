<template>
  <el-card class="current-contents-list el-card--table">
    <div slot="header">
      <h3>{{ $t('home.contentsList.title') }}</h3>
    </div>
    <el-table :data="contents">
      <template slot="empty">
        <img src="~assets/images/empty/img-content-empty.jpg" />
        <p>{{ $t('home.contentsList.empty') }}</p>
      </template>
    </el-table>
  </el-card>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      contents: [],
    }
  },
}
</script>

<style lang="scss"></style>
