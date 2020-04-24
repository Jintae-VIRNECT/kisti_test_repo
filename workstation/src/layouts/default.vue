<template>
  <div>
    <header>
      <the-header :showSection="showSection">
        <template slot="subTitle">
          <el-divider direction="vertical" />
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${activeWorkspace.info.profile})`"
            />
          </div>
          <span>{{ activeWorkspace.info.name }}</span>
        </template>
      </the-header>
    </header>
    <div>
      <the-sidebar :menus="sideMenus" :bottomMenus="sideBottomMenus" />
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideMenus, sideBottomMenus } from '@/models/layout'
import { mapGetters } from 'vuex'

export default {
  middleware: 'default',
  components: {
    TheSidebar,
    TheHeader,
  },
  data() {
    return {
      showSection: {
        profile: true,
      },
      sideMenus,
      sideBottomMenus,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
}
</script>

<style lang="scss">
:not(.no-sidebar) > .header-section {
  left: $the-sidebar-width !important;
  width: calc(100% - #{$the-sidebar-width}) !important;
}
.sub-title {
  font-size: 14px;
  .el-divider {
    height: 24px;
    margin-right: 16px;
  }
  .avatar {
    display: inline-block;
    width: 22px;
    height: 22px;
    margin-right: 4px;
    vertical-align: middle;
  }
  span {
    display: inline-block;
    vertical-align: middle;
  }
}
</style>
