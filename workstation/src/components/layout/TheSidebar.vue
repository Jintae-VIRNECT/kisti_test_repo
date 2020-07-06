<template>
  <nav class="the-sidebar">
    <div class="the-sidebar__inner">
      <div class="the-sidebar__logo">
        <a
          @click.stop="openCollapse(logoCollapse)"
          :style="`background-image: url(${activeWorkspace.profile})`"
        />
      </div>
      <div class="the-sidebar__upper">
        <the-sidebar-menu-list :menus="menus" @openCollapse="openCollapse" />
      </div>
      <div class="the-sidebar__lower">
        <the-sidebar-menu-list
          :menus="bottomMenus"
          @openCollapse="openCollapse"
        />
      </div>
    </div>
    <transition name="collapse">
      <div v-if="showCollapse" class="the-sidebar__collapse" @click.stop>
        <keep-alive>
          <component :is="collapseComponent" @closeCollapse="closeCollapse" />
        </keep-alive>
      </div>
    </transition>
  </nav>
</template>

<script>
import collapseWorkspace from '@/components/layout/collapses/TheSidebarCollapseWorkspace'
import collapseTask from '@/components/layout/collapses/TheSidebarCollapseTask'
import collapseMember from '@/components/layout/collapses/TheSidebarCollapseMember'
import collapseContents from '@/components/layout/collapses/TheSidebarCollapseContents'
import TheSidebarMenuList from './TheSidebarMenuList.vue'
import { mapGetters } from 'vuex'

export default {
  components: {
    TheSidebarMenuList,
    collapseWorkspace,
    collapseTask,
    collapseMember,
    collapseContents,
  },
  props: {
    menus: Array,
    bottomMenus: Array,
  },
  data() {
    return {
      showCollapse: false,
      collapseComponent: null,
      logoCollapse: 'collapseWorkspace',
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  methods: {
    openCollapse(component) {
      this.collapseComponent = component
      this.showCollapse = true
    },
    closeCollapse() {
      this.showCollapse = false
    },
  },
  mounted() {
    document.addEventListener('click', this.closeCollapse)
  },
}
</script>

<style lang="scss">
$the-sidebar-background: #172b4d;
$the-sidebar-border: solid 1px #0d1d39;

.the-sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 2001;
  width: $the-sidebar-width;
  height: 100vh;
  background-color: $the-sidebar-background;
  & + main {
    padding-left: $the-sidebar-width;
  }
  a {
    cursor: pointer;
  }
}
.the-sidebar__logo {
  width: 36px;
  height: 36px;
  margin: 12px;
  overflow: hidden;
  border-radius: 50%;

  & > a {
    display: block;
    width: 100%;
    height: 100%;
    background-color: #e2e7ed;
    background-position: center;
    background-size: cover;
  }
}
.the-sidebar__inner {
  position: relative;
  z-index: 2002;
  height: 100%;
  background-color: $the-sidebar-background;
  border-right: $the-sidebar-border;
}
.the-sidebar__upper {
  margin-top: 24px;
}
.the-sidebar__lower {
  position: absolute;
  bottom: 0;
  width: 100%;
  margin-bottom: 16px;
}
.the-sidebar__collapse {
  position: absolute;
  top: 0;
  left: $the-sidebar-width;
  z-index: 2001;
  width: 240px;
  height: 100%;
  color: #fff;
  background: $the-sidebar-background;

  &__header {
    padding: 21px 20px;
    line-height: 20px;
    border-bottom: $the-sidebar-border;
  }
  &__body {
    margin: 12px;
  }
}
.the-sidebar__collapse--default .the-sidebar__collapse__header {
  & > * {
    display: inline-block;
    vertical-align: middle;
  }
  & > img {
    width: 18px;
    margin-right: 4px;
  }
}
.the-sidebar__collapse--default .the-sidebar__collapse__body {
  & > a {
    display: block;
    width: 100%;
    height: 38px;
    margin: 4px 0;
    padding: 0 12px;
    overflow: hidden;
    color: rgba(255, 255, 255, 0.75);
    text-align: left;
    border-radius: 4px;

    & > span {
      display: inline-block;
      width: 144px;
      overflow: hidden;
      line-height: 38px;
      white-space: nowrap;
      text-overflow: ellipsis;
      vertical-align: middle;
    }
    &:hover {
      color: #fff;
      background: rgba(44, 62, 93, 0.6);
    }
    &:active,
    &.nuxt-link-exact-active {
      color: #fff;
      background: #324461;
    }
  }
}

.collapse-enter {
  transform: translateX(-100%);
}
.collapse-enter-to {
  transform: translateX(0);
  transition: transform 0.3s;
}
.collapse-leave-active {
  transform: translateX(-100%);
  transition: transform 0.3s;
}
</style>
