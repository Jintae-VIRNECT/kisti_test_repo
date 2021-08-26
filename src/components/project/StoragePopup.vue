<template>
  <el-popover
    class="project-storage-popup"
    placement="bottom-end"
    :title="$t('projects.storage.info.title')"
    trigger="click"
    visible-arrow="false"
    width="312"
  >
    <el-progress :percentage="30" :show-text="false" :stroke-width="12" />
    <ProjectPopupItem
      :title="$t('projects.storage.info.allVolume')"
      byte="214748364800"
      color="#e6e9ee"
    />
    <ProjectPopupItem
      :title="$t('projects.storage.info.allUseVolume')"
      byte="150860726272"
      color="#ffb854"
    />
    <ProjectPopupItem
      :title="$t('projects.storage.info.projectUseVolume')"
      byte="52291226828.8"
      color="#007cfe"
    />
    <el-button type="primary" slot="reference">
      {{ $t('projects.storage.info.freeVolume') }} :
      {{ 150555500000 | formatBytes }}
    </el-button>
  </el-popover>
</template>

<script>
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    data: Object,
  },
  data() {
    return {
      showMe: true,
    }
  },
  mounted() {
    const progressOuter = document.getElementsByClassName(
      'el-progress-bar__outer',
    )[0]
    const progressInnerAll = document.getElementsByClassName(
      'el-progress-bar__inner',
    )[0]
    const progressInnerAllUse = progressInnerAll.cloneNode(true)
    progressInnerAllUse.style.width = '60%'
    const progressInnerUseProject = progressInnerAll.cloneNode(true)
    progressInnerUseProject.style.width = '100%'
    progressOuter.appendChild(progressInnerAllUse)
    progressOuter.appendChild(progressInnerUseProject)
  },
}
</script>

<style lang="scss">
body .el-popover {
  padding: 14px 20px 20px;

  .el-popover__title {
    @include fontLevel(75);
    color: $font-color-content;
  }

  .el-progress {
    margin: 16px 0;
    &-bar__inner {
      border-radius: 0px;
    }

    $widths: 40%, 80%, 100%;
    $colors: #007cfe, #ffb854, #e6e9ee;
    $zIndexs: 2, 1, 0;

    @for $i from 1 through 3 {
      &-bar__inner:nth-child(#{$i}) {
        width: nth($widths, $i);
        background: nth($colors, $i);
        z-index: nth($zIndexs, $i);
      }
    }
  }
}
</style>
