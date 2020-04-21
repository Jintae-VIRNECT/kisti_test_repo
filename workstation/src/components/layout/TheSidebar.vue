<template>
  <nav class="the-sidebar">
    <div class="the-sidebar__logo">
      <img :src="logo" />
    </div>
    <div class="the-sidebar__inner">
      <div class="the-sidebar__upper">
        <the-sidebar-menu-list :menus="menus" />
      </div>
      <div class="the-sidebar__lower">
        <the-sidebar-menu-list :menus="bottomMenus" />
      </div>
    </div>
  </nav>
</template>

<script>
import TheSidebarMenuList from './TheSidebarMenuList.vue'
export default {
  components: {
    TheSidebarMenuList,
  },
  props: {
    logo: Object,
    menus: Array,
    bottomMenus: Array,
  },
}
</script>

<style lang="scss">
.the-sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  width: $the-sidebar-width;
  height: 100vh;
  background-color: #1b293e;
  & + div {
    padding-left: $the-sidebar-width;
  }
}
.the-sidebar__inner {
  height: 100%;
}
.the-sidebar__lower {
  position: absolute;
  bottom: 0;
  width: 100%;
}
</style>
