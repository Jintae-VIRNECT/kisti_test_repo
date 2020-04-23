<template>
  <el-card class="storage-info">
    <div slot="header">
      <h3>{{ $t('home.storage.title') }}</h3>
      <router-link to="/">{{ $t('home.storage.link') }}</router-link>
    </div>
    <div>
      <div class="stats">
        <span>{{ $t('home.storage.allStorageVolume') }}</span>
        <span class="count">{{ $t('home.storage.unit') }}</span>
      </div>
      <div class="detail">
        <span>{{ $t('home.storage.storageVolume') }}</span>
        <el-progress :percentage="50" />
        <span>{{ $t('home.storage.used') }}</span>
        <span>{{ $t('home.storage.available') }}</span>
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
