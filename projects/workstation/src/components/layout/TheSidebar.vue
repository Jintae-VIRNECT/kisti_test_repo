<template>
  <nav class="sidebar">
    <div class="sidebar-inner" @click.stop="">
      <h1 class="sidebar--logo">
        <a class="popover--button" href="/"
          ><img src="~assets/image/lnb-logo-smart-factory.png" alt="Remote"
        /></a>
      </h1>
      <div class="sidebar--tools">
        <ul class="sidebar--mode">
          <li
            class="sidebar--item"
            v-for="menu of menus"
            :key="menu.path"
            :class="{
              current: isCurrentMenu(menu.path),
              active: isActiveMenu(menu),
            }"
          >
            <el-tooltip
              class="item"
              effect="dark"
              :content="menu.label"
              placement="right"
            >
              <router-link :to="menu.path"
                ><button
                  class="sidebar--item__button"
                  @mouseover="currentMenuPath = menu.path"
                  @mouseout="currentMenuPath = null"
                >
                  <img :src="menu.image" :alt="menu.label" /></button
              ></router-link>
            </el-tooltip>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  props: {
    menus: Array,
  },
  data() {
    return {
      currentMenuPath: '',
      currentSubMenuPath: '',
      activeMenuPath: '',
      activeSubMenuPath: '',
    }
  },
  watch: {
    $route(to) {
      const paths = to.path.split('/')
      this.activeMenuPath = '/' + paths[1]
      this.currentSubMenuPath = to.path
    },
  },
  methods: {
    showSubMenu(menu) {
      if (this.openedMenu === menu) {
        this.openedMenu = false
      } else {
        this.openedMenu = menu
      }
    },
    isCurrentMenu(path) {
      return path == this.currentMenuPath
    },
    isActiveMenu(menu) {
      if (menu.pathAlias) return menu.pathAlias.includes(this.activeMenuPath)
      return menu.path == this.activeMenuPath
    },
    isActiveSubMenu(path) {
      return path == this.currentSubMenuPath
    },
    resetSubmenu() {
      this.currentMenuPath = ''
    },
  },
  created() {
    const paths = this.$route.path.split('/')
    this.activeMenuPath = '/' + paths[1]
  },
}
</script>

<style lang="scss">
.sidebar__logo {
  width: 44px;
  height: 44px;
  background-image: linear-gradient(332deg, #225bac 90%, #276ac7 6%);
}
.sidebar--mode .sidebar--item {
  .sidebar--item__button {
    width: 40px;
    height: 40px;
    opacity: 0.75;
  }
  &.current .sidebar--item__button {
    background: none;
    img {
      opacity: 1;
    }
  }
  &.active .sidebar--item__button {
    background: url('~assets/image/ic-bg.svg') no-repeat;
    background-color: rgba(0, 0, 0, 0);
    img {
      opacity: 1;
    }
  }
}
</style>
