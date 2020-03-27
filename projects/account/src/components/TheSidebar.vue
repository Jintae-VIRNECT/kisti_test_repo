<template>
  <nav class="the-sidebar">
    <el-menu :default-active="now" :router="true" :collapse="isCollapse">
      <el-menu-item
        v-for="(menu, index) in menus"
        :key="menu.path"
        :index="index + ''"
        :route="menu.path"
      >
        <i class="el-icon-setting"></i>
        <span slot="title">{{ $t(menu.label) }}</span>
      </el-menu-item>
    </el-menu>
  </nav>
</template>

<script>
export default {
  props: {
    /**
     * [{ path, image, label }, ...]
     */
    menus: Array,
  },
  data() {
    return {
      now: '0',
      isCollapse: false,
    }
  },
  beforeMount() {
    const now = this.$router.currentRoute.path
    this.now = this.menus.findIndex(menu => menu.path === now) + ''
  },
}
</script>

<style lang="scss">
$the-sidebar-width: 240px;
$the-sidebar-item-height: 40px;

.the-sidebar {
  position: fixed;
  z-index: 9;
  height: 100vh;
  border-right: 1px solid #eaedf3;

  & + main {
    position: relative;
    margin-left: $the-sidebar-width;
  }
}
.the-sidebar .el-menu {
  height: 100%;
  padding: 12px;
  border-right: none;

  &:not(.el-menu--collapse) {
    width: $the-sidebar-width;
  }

  .el-menu-item {
    height: $the-sidebar-item-height;
    line-height: calc(#{$the-sidebar-item-height} - 3px);
  }
}
</style>
