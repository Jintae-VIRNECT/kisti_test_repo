<template>
  <ul>
    <li class="the-sidebar__menus__item" v-for="menu of menus" :key="menu.path">
      <el-divider v-if="menu.divider" />
      <el-tooltip
        v-else
        class="item"
        effect="dark"
        :content="menu.label"
        placement="right"
      >
        <router-link :to="menu.path">
          <img :src="menu.image" :alt="menu.label" />
        </router-link>
      </el-tooltip>
    </li>
  </ul>
</template>

<script>
export default {
  props: {
    menus: Array,
  },
}
</script>

<style lang="scss">
.the-sidebar__menus__item {
  width: 100%;
  margin: 16px 0;
  padding: 0 12px;

  & > a {
    display: block;
    width: 100%;
    padding: 7px;
    line-height: 0;
    background-repeat: no-repeat;
    background-size: 100%;
    mask: url('~assets/images/icon/ic-bg.svg');
    mask-size: 100%;

    & > img {
      width: 100%;
      opacity: 0.65;
    }

    &:hover {
      background: rgba(65, 81, 109, 0.7);
      & > img {
        opacity: 0.9;
      }
    }
    &.nuxt-link-exact-active {
      background: #465875;
      & > img {
        opacity: 1;
      }
    }
  }

  .el-divider {
    width: 30px;
    margin: 0 auto;
    background: rgba(255, 255, 255, 0.2);
  }
}
</style>
