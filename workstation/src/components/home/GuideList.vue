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
        v-for="link in guide"
        :href="$t(link.path)"
        :key="link.path"
        target="_blank"
      >
        <img v-if="link.image" :src="link.image" />
        <span>{{ $t(link.label) }}</span>
        <img src="~assets/images/icon/ic-file-download.svg" />
      </a>
    </div>
  </el-card>
</template>

<script>
import { guide } from '@/models/home'

export default {
  data() {
    return {
      guide,
    }
  },
}
</script>
