<template>
  <el-popover
    popper-class="project-storage-popup"
    placement="bottom-end"
    :title="$t('projects.storage.info.title')"
    trigger="click"
    visible-arrow="false"
    width="312"
  >
    <el-progress
      ref="storagePopup"
      :percentage="ProjectUseVolume"
      :show-text="false"
      :stroke-width="12"
    />
    <ProjectPopupItem
      :title="$t('projects.storage.info.allVolume')"
      :byte="plansInfo.storage.max"
      color="#e6e9ee"
    />
    <ProjectPopupItem
      :title="$t('projects.storage.info.allUseVolume')"
      :byte="plansInfo.storage.current"
      color="#ffb854"
    />
    <ProjectPopupItem
      :title="$t('projects.storage.info.projectUseVolume')"
      :byte="plansInfo.project.current"
      color="#007cfe"
    />
    <el-button type="primary" slot="reference" @click="setInnerProgressbar">
      {{ $t('projects.storage.info.freeVolume') }} :
      {{ plansInfo.storage.remain }} GB
    </el-button>
  </el-popover>
</template>

<script>
import filters from '@/mixins/filters'
import { mapGetters } from 'vuex'

export default {
  mixins: [filters],
  data() {
    return {
      progressInnerProjectUse: null,
      progressInnerAllUse: null,
      progressInnerAllVolume: null,
      ProjectUseVolume: 0,
    }
  },
  computed: {
    ...mapGetters({
      plansInfo: 'plan/plansInfo',
    }),
  },
  methods: {
    // 스토리지 el-progress의 inner Bar 엘레먼트를 2개 추가생성합니다.
    innerProgressBarCreate() {
      const [progressBar] = this.$refs.storagePopup.$el.children
      const [progressOuter] = progressBar.children

      // 프로젝트 사용량 inner bar
      this.progressInnerProjectUse = progressOuter.children[0]
      // 총 사용량 inner bar
      this.progressInnerAllUse = this.progressInnerProjectUse.cloneNode(true)
      // 전체 용량 inner bar
      this.progressInnerAllVolume = this.progressInnerProjectUse.cloneNode(true)

      progressOuter.appendChild(this.progressInnerAllUse)
      progressOuter.appendChild(this.progressInnerAllVolume)
    },
    // progressBar의 각 용량별 width를 지정해줍니다.
    setInnerProgressbar() {
      this.ProjectUseVolume = this.plansInfo.project.percent
      this.progressInnerAllUse.style.width = `${this.plansInfo.storage.percent}%`
      this.progressInnerAllVolume.style.width = '100%'
    },
  },
  mounted() {
    this.innerProgressBarCreate()
  },
}
</script>

<style lang="scss">
.project-storage-popup {
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
