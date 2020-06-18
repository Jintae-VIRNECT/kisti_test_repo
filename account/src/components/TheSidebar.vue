<template>
  <nav class="the-sidebar">
    <el-menu :default-active="now" :router="true" :collapse="isCollapse">
      <el-menu-item
        v-for="(menu, index) in menus"
        :key="menu.path"
        :index="index + ''"
        :route="menu.path"
      >
        <i class="icon" :style="`mask-image: url(${menu.image})`" />
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
  watch: {
    $route() {
      this.detectActive()
    },
  },
  methods: {
    detectActive() {
      const now = this.$router.currentRoute.path
      this.now = this.menus.findIndex(menu => menu.path === now) + ''
    },
  },
  mounted() {
    this.detectActive()
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
    color: #5e6b81;
    line-height: calc(#{$the-sidebar-item-height} - 3px);
    border-radius: 3px;

    &:hover {
      background: #f5f7fa;
    }

    .icon {
      display: inline-block;
      width: 24px;
      height: 24px;
      margin-right: 10px;
      background: #919db0;
    }
  }
  .el-menu-item.is-active {
    color: #1468e2;
    background: #dfedff;
    .icon {
      background: #0f75f5;
    }
  }
}
</style>
