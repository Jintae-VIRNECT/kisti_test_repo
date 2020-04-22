<template>
  <nav class="the-sidebar">
    <div class="the-sidebar__logo">
      <!-- 링크 -->
      <router-link v-if="logo.path[0] === '/'" :to="logo.path">
        <img :src="logo.image" :alt="logo.label" />
      </router-link>
      <!-- 컴포넌트 -->
      <a v-if="logo.path[0] === '@'" @click.stop="openCollapse(logo.path)">
        <img :src="logo.image" :alt="logo.label" />
      </a>
    </div>
    <div class="the-sidebar__inner">
      <div class="the-sidebar__upper">
        <the-sidebar-menu-list :menus="menus" />
      </div>
      <div class="the-sidebar__lower">
        <the-sidebar-menu-list :menus="bottomMenus" />
      </div>
    </div>
    <div v-if="showCollapse" class="the-sidebar__collapse" @click.stop>
      <keep-alive>
        <component :is="collapseComponent" />
      </keep-alive>
    </div>
  </nav>
</template>

<script>
import collapseWorkspace from '@/components/layout/collapses/TheSidebarCollapseWorkspace'

import TheSidebarMenuList from './TheSidebarMenuList.vue'
export default {
  components: {
    TheSidebarMenuList,
    collapseWorkspace,
  },
  props: {
    logo: Object,
    menus: Array,
    bottomMenus: Array,
  },
  data() {
    return {
      showCollapse: false,
      collapseComponent: null,
    }
  },
  methods: {
    openCollapse(component) {
      this.collapseComponent = component.substr(1)
      console.log(this.collapseComponent)
      this.showCollapse = true
    },
  },
  mounted() {
    document.addEventListener('click', () => {
      this.showCollapse = false
    })
  },
}
</script>

<style lang="scss">
$the-sidebar-background: #172b4d;

.the-sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 10;
  width: $the-sidebar-width;
  height: 100vh;
  background-color: $the-sidebar-background;
  border-right: solid 1px #0d1d39;
  & + main {
    padding-left: $the-sidebar-width;
  }
}
.the-sidebar__logo {
  width: 36px;
  height: 36px;
  margin: 12px;
  border: solid 2px rgba(191, 212, 255, 0.4);
  border-radius: 50%;

  & > a {
    display: block;
    width: 100%;
    height: 100%;
  }
}
.the-sidebar__inner {
  height: 100%;
  margin-top: 24px;
}
.the-sidebar__lower {
  position: absolute;
  bottom: 0;
  width: 100%;
}
.the-sidebar__collapse {
  position: absolute;
  top: 0;
  left: $the-sidebar-width;
  width: 240px;
  height: 100%;
  background: $the-sidebar-background;
}
</style>
