<template>
  <nav class="the-sidebar">
    <div class="the-sidebar__inner">
      <h1 class="the-sidebar__logo">
        <nuxt-link :to="logo.path">
          <img :src="logo.image" />
        </nuxt-link>
      </h1>
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
$sidebar-width: 60px;

.the-sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  width: $sidebar-width;
  height: 100vh;

  & + div {
    padding-left: $sidebar-width;
  }
}
.the-sidebar__inner {
  height: 100%;
  background-color: #1b293e;
}
.the-sidebar__logo {
  img {
    width: 100%;
    padding: 8px 6px;
  }
}
.the-sidebar__lower {
  position: absolute;
  bottom: 0;
  width: 100%;
}
</style>
