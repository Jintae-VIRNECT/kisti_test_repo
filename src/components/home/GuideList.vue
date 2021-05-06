<template>
  <el-card class="link-list-card">
    <div slot="header">
      <h3>
        <img src="~assets/images/icon/ic-local-library.svg" />
        <span>{{ $t('guide.title') }}</span>
      </h3>
    </div>

    <!-- 링크 타입 -->
    <div>
      <a
        v-for="{ label, url } in this.$t('guide.list')"
        v-if="url"
        :href="url"
        :key="label"
        target="_blank"
      >
        <span>{{ label }}</span>
        <img src="~assets/images/icon/ic-file-download.svg" />
      </a>
    </div>
  </el-card>
</template>

<script>
export default {}
</script>
