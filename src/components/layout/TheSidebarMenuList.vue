<template>
  <ul>
    <li class="the-sidebar__menus__item" v-for="menu of menus" :key="menu.path">
      <el-divider v-if="menu.divider" />
      <el-tooltip
        v-else
        popper-class="the-sidebar__tooltip"
        effect="dark"
        :content="$t(menu.label)"
        placement="right"
      >
        <!-- 컴포넌트 -->
        <nuxt-link
          v-if="menu.collapse"
          :to="menu.path"
          event=""
          @click.native.stop="openCollapse(menu.collapse)"
        >
          <img :src="menu.image" :alt="$t(menu.label)" />
          <span>{{ $t(menu.label) }}</span>
        </nuxt-link>
        <!-- 링크 -->
        <nuxt-link
          v-else
          :to="menu.path"
          @click.native="$emit('closeCollapse')"
        >
          <img :src="menu.image" :alt="$t(menu.label)" />
          <span>{{ $t(menu.label) }}</span>
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
  methods: {
    openCollapse(component) {
      this.$emit('openCollapse', component)
    },
  },
}
</script>

<style lang="scss">
.the-sidebar__menus__item {
  width: 100%;
  margin: 16px 0;
  padding: 0 12px;

  & > a {
    position: relative;
    display: block;
    width: 100%;
    padding: 7px;
    line-height: 0;
    background-repeat: no-repeat;
    background-size: 100%;
    border-radius: 4px;
    mask: url('~assets/images/icon/ic-bg.svg');
    mask-size: 100%;
    mask-position: center;

    & > img {
      width: 21px;
      opacity: 0.65;
    }
    & > span {
      position: absolute;
      width: 200px;
      margin: auto 18px;
      overflow: hidden;
      color: #fff;
      line-height: 22px;
      opacity: 0.75;
    }

    &:hover {
      background: rgba(65, 81, 109, 0.7);
      & > img,
      & > span {
        opacity: 0.9;
      }
    }
    &.nuxt-link-active:not([href='/']),
    &.nuxt-link-exact-active {
      background: #465875;
      & > img {
        opacity: 1;
      }
    }
  }

  .el-divider {
    width: 100%;
    margin: 0 auto;
    background: rgba(255, 255, 255, 0.2);
  }
}

.the-sidebar__tooltip {
  transform: translateX(8px);
}

.the-sidebar__inner--wide .the-sidebar__menus__item {
  & > a {
    mask-size: cover;
  }
}
</style>
