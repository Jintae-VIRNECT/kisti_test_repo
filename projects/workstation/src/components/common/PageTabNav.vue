<template>
  <div class="page-nav">
    <div class="page-nav--left">
      <el-tabs class="page-tab-nav" v-model="activeTab" @tab-click="pathToTab">
        <el-tab-pane
          v-for="m in model"
          :key="m.to"
          :label="m.title"
          :name="m.to"
        >
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="page-nav--right">
      <slot name="page-nav--right"></slot>
    </div>
  </div>
</template>
<script>
const model = [
  { title: '멤버', to: '/members' },
  { title: '콘텐츠', to: '/contents' },
  { title: '공정', to: '/process' },
]
export default {
  data() {
    return {
      model,
      activeTab: '/' + this.$route.path.split('/')[1],
    }
  },
  methods: {
    pathToTab({ name }) {
      this.$router.push(name)
    },
  },
}
</script>
