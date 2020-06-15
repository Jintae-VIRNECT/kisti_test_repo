<template>
  <el-card class="link-list-card">
    <div slot="header">
      <h3>
        <img :src="icon" />
        <span>{{ title }}</span>
      </h3>
    </div>
    <!-- 링크 타입 -->
    <div v-if="type === 'link'">
      <a
        v-for="link in links"
        :href="link.path"
        :key="link.path"
        target="_blank"
      >
        <img v-if="link.image" :src="link.image" />
        <span>{{ link.label }}</span>
        <img src="~assets/images/icon/ic-arrow-forward.svg" />
      </a>
    </div>
    <!-- 다운로드 타입 -->
    <div v-if="type === 'download'">
      <a v-for="link in links" :href="link.path" :key="link.path" download>
        <img v-if="link.image" :src="link.image" />
        <span>{{ link.label }}</span>
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
    padding: 8px 24px;
  }
  a {
    position: relative;
    display: flex;
    align-items: center;
    height: 44px;

    & > img:first-child {
      margin-right: 4px;
    }
    & > span {
      color: $font-color-content;
    }
    & > img:last-child {
      position: absolute;
      right: 0;
    }
  }
}
</style>
