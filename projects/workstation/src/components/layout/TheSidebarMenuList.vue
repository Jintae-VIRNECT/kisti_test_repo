<template>
  <ul>
    <li class="the-sidebar__menus__item" v-for="menu of menus" :key="menu.path">
      <el-tooltip
        class="item"
        effect="dark"
        :content="menu.label"
        placement="right"
      >
        <nuxt-link :to="menu.path">
          <img :src="menu.image" :alt="menu.label" />
        </nuxt-link>
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
  padding: 10px;

  & > a {
    display: block;
    width: 100%;
    padding: 8px;
    background-repeat: no-repeat;
    background-size: 100%;

    &.nuxt-link-exact-active {
      background-image: url('~assets/image/ic-bg.svg');
    }

    & > img {
      width: 100%;
    }
  }
}
</style>
