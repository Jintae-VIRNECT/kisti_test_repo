<template>
  <el-card class="storage-info">
    <div slot="header">
      <h3>{{ $t('home.download.title') }}</h3>
      <router-link to="/">
        {{ $t('home.download.link') }}
      </router-link>
    </div>
    <div>
      <div class="stats">
        <span>{{ $t('home.download.allDownloadedVolume') }}</span>
        <span class="count">{{ $t('home.download.unit') }}</span>
      </div>
      <div class="detail">
        <span>{{ $t('home.download.downloadedVolume') }}</span>
        <el-progress :percentage="50" />
        <span>{{ $t('home.download.downloaded') }}</span>
        <span>{{ $t('home.download.available') }}</span>
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
