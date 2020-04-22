<template>
  <el-card class="storage-info">
    <div slot="header">
      <h3>{{ $t('main.download.title') }}</h3>
      <router-link to="/">
        {{ $t('main.download.link') }}
      </router-link>
    </div>
    <div>
      <div class="stats">
        <span>{{ $t('main.download.allDownloadedVolume') }}</span>
        <span class="count">{{ $t('main.download.unit') }}</span>
      </div>
      <div class="detail">
        <span>{{ $t('main.download.downloadedVolume') }}</span>
        <el-progress :percentage="50" />
        <span>{{ $t('main.download.downloaded') }}</span>
        <span>{{ $t('main.download.available') }}</span>
      </div>
    </div>
  </el-card>
</template>

<script>
export default {
  props: {
    title: String,
  },
}
</script>

<style lang="scss"></style>
