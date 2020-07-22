<template>
  <el-card class="link-list-card">
    <div slot="header">
      <h3>
        <img :src="icon" />
        <span>{{ title }}</span>
      </h3>
    </div>
    <!-- 링크 타입 -->
    <div>
      <a
        v-for="link in links"
        :href="link.path"
        :key="link.path"
        target="_blank"
        :download="link.isDownload"
      >
        <img v-if="link.image" :src="link.image" />
        <span>{{ link.label }}</span>
        <img
          v-if="link.isDownload"
          src="~assets/images/icon/ic-file-download.svg"
        />
        <img v-else src="~assets/images/icon/ic-arrow-forward.svg" />
      </a>
    </div>
  </el-card>
</template>

<script>
export default {
  props: {
    title: String,
    icon: String,
    type: String,
    links: Array,
  },
}
</script>

<style lang="scss">
#__nuxt .link-list-card {
  .el-card__body {
    padding: 8px 0;
  }
  a {
    position: relative;
    display: flex;
    align-items: center;
    height: 44px;
    padding: 0 24px;
    transition: background-color 0.25s ease;
    &:hover {
      background-color: #f5f7fa;
    }

    & > img:first-child {
      margin-right: 4px;
      margin-left: -8px;
    }
    & > span {
      color: $font-color-content;
    }
    & > img:last-child {
      position: absolute;
      right: 24px;
    }
  }
}
</style>
