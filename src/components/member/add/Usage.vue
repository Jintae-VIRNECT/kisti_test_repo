<template>
  <div class="usage">
    <img src="~assets/images/icon/ic-person.svg" />
    <div class="usage__counter">
      <span>{{ availableMember }}</span>
      <span>/ {{ maximum }}</span>
    </div>
    <el-tooltip
      :content="$t('members.create.workspaceTooltip')"
      placement="right"
    >
      <img src="~assets/images/icon/ic-error.svg" />
    </el-tooltip>
  </div>
</template>

<script>
export default {
  props: ['availableMember', 'maximum'],
}
</script>

<style lang="scss" scoped>
#__nuxt .usage {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  img:first-child {
    width: 20px;
    height: 20px;
    opacity: 0.6;
  }
  &__counter {
    span {
      @include fontLevel(75);
    }
    span:first-child {
      color: #0052cc;
    }
    span:last-child {
      margin-right: 7px;
    }
  }
}
</style>
