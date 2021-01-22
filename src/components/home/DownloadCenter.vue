<template>
  <el-card class="link-list-card">
    <div slot="header">
      <h3>
        <img src="~assets/images/icon/ic-phonelink.svg" />
        <span>{{ $t('home.install.title') }}</span>
      </h3>
    </div>

    <div>
      <a
        v-for="link in install"
        :href="$url.download + link.path"
        :key="link.path"
        target="_blank"
      >
        <img v-if="link.image" :src="link.image" />
        <span>{{ $t(link.label) }}</span>
        <img src="~assets/images/icon/ic-arrow-forward.svg" />
      </a>
    </div>
  </el-card>
</template>

<script>
import { install } from '@/models/home'

export default {
  data() {
    return {
      install,
    }
  },
}
</script>
