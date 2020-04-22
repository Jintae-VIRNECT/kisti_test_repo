<template>
  <el-card class="storage-info">
    <div slot="header">
      <h3>{{ $t('main.storage.title') }}</h3>
      <router-link to="/">{{ $t('main.storage.link') }}</router-link>
    </div>
    <div>
      <div class="stats">
        <span>{{ $t('main.storage.allStorageVolume') }}</span>
        <span class="count">{{ $t('main.storage.unit') }}</span>
      </div>
      <div class="detail">
        <span>{{ $t('main.storage.storageVolume') }}</span>
        <el-progress :percentage="50" />
        <span>{{ $t('main.storage.used') }}</span>
        <span>{{ $t('main.storage.available') }}</span>
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
