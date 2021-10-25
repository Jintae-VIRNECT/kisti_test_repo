<template>
  <div class="tab-workspace-sub-group">
    <div class="tab-workspace-sub-group__header">
      <span class="tab-workspace-sub-group__header--title">{{
        $t('그룹 설정')
      }}</span>
      <span class="tab-workspace-sub-group__header--description">
        {{ $t('워크스페이스의 하위 그룹을 설정합니다. (최대 10개)') }}
      </span>
      <span class="tab-workspace-sub-group__header--etc">미설정 멤버 : 00</span>
      <button
        @click="addSubGroup"
        class="tab-workspace-sub-group__header--addgroup"
      >
        그룹 추가
      </button>
    </div>

    <!-- list + paging + header-->
    <workspace-sub-group-list
      :isOverflow="isOverflow"
    ></workspace-sub-group-list>
  </div>
</template>

<script>
import WorkspaceSubGroupList from '../workspaceSubGroup/section/WorkspaceSubGroupList'
export default {
  name: 'TabWorkspaceSubGroup',
  components: { WorkspaceSubGroupList },
  data() {
    return {
      isOverflow: false,
    }
  },
  methods: {
    addSubGroup() {
      console.log('add group')
      this.$eventBus.$emit('open::addSubGroupList')
    },
    checkIsOverflow() {
      if (matchMedia('only screen  and (max-width: 1372px)').matches) {
        this.isOverflow = true
      } else if (matchMedia('screen and (max-width: 920px)').matches) {
        this.isOverflow = true
      } else {
        this.isOverflow = false
      }
    },
  },
  mounted() {
    this.checkIsOverflow()
    window.addEventListener('resize', this.checkIsOverflow)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkIsOverflow)
  },
}
</script>

<style lang="scss">
.tab-workspace-sub-group {
  margin-top: 44px;
}

.tab-workspace-sub-group__header {
  position: relative;
  display: flex;
  align-items: center;
}

.tab-workspace-sub-group__header--title {
  margin-right: 16px;
  margin-bottom: 4px;
  color: rgb(30, 30, 30);
  font-weight: 500;
  font-size: 22px;
}

.tab-workspace-sub-group__header--description {
  color: rgb(122, 122, 122);
  font-weight: normal;
  font-size: 15px;
}

.tab-workspace-sub-group__header--etc {
  position: absolute;
  right: 111px;
  color: rgb(58, 74, 108);
  font-weight: 500;
  font-size: 15px;
}

.tab-workspace-sub-group__header--addgroup {
  position: absolute;
  right: 0;
  width: 91px;
  height: 38px;
  color: rgb(117, 127, 145);

  font-weight: 500;
  font-size: 15px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 4px;
}
</style>
