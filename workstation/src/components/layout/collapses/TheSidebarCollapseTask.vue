<template>
  <div class="the-sidebar__collapse--task">
    <div class="the-sidebar__collapse__header">
      <h5>{{ $t('menu.tasks') }}</h5>
    </div>
    <div class="the-sidebar__collapse__body">
      <nuxt-link to="/tasks" @click.native="$emit('closeCollapse')">
        <span>{{ $t('menu.collapse.tasks.list') }}</span>
      </nuxt-link>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss">
.the-sidebar__collapse--task .the-sidebar__collapse__body {
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
</style>
