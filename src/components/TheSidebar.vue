<template>
  <nav class="the-sidebar">
    <el-menu :default-active="now" :router="true">
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
  width: $the-sidebar-width;
  height: 100%;
  padding: 12px;
  border-right: none;

  .el-menu-item {
    height: $the-sidebar-item-height;
    padding: 0 12px !important;
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
      margin-right: 14px;
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

@media screen and (max-width: 840px) {
  .the-sidebar {
    $width-mobile: 70px;
    .el-menu {
      width: $width-mobile;
      overflow: hidden;
    }
    .el-menu-item > span {
      display: none;
    }
    & + main {
      margin-left: $width-mobile;
    }
  }
}
</style>
