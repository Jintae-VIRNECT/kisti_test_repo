<template>
  <el-card class="member-profile-card">
    <div slot="header">
      <h3>{{ title }}</h3>
    </div>
    <!-- 링크 타입 -->
    <div v-if="type === 'link'">
      <a v-for="link in links" :href="link.path" :key="link.path">
        <span>{{ link.label }}</span>
      </a>
    </div>
    <!-- 다운로드 타입 -->
    <div v-if="type === 'download'">
      <a v-for="link in links" :href="link.path" :key="link.path" download>
        <span>{{ link.label }}</span>
      </a>
    </div>
  </el-card>
</template>

<script>
export default {
  props: {
    title: String,
    type: String,
    links: Array,
  },
}
</script>

<style lang="scss">
.member-profile-card {
  a {
    display: block;
  }
}
</style>
