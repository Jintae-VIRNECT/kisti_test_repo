<template>
  <div class="tab-workspace-sub-group">
    <div class="tab-workspace-sub-group__header">
      <span class="tab-workspace-sub-group__header--title">{{
        $t('subgroup.setting')
      }}</span>
      <span class="tab-workspace-sub-group__header--description">
        {{ $t('subgroup.description') }}
      </span>
      <span class="tab-workspace-sub-group__header--etc"
        >{{ $t('subgroup.etc_member_count') }} {{ etcMemberCount }}</span
      >
      <button
        @click="addSubGroup"
        :disabled="disableAddGroup"
        class="tab-workspace-sub-group__header--addgroup"
        :class="{ disabled: disableAddGroup }"
      >
        {{ $t('subgroup.add_group') }}
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
      etcMemberCount: 0,
      subGroupCount: 0,
    }
  },
  computed: {
    disableAddGroup() {
      if (this.etcMemberCount === 0 || this.subGroupCount >= 10) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    checkIsOverflow() {
      if (matchMedia('only screen  and (max-width: 1372px)').matches) {
        this.isOverflow = true
      } else if (matchMedia('screen and (max-width: 920px)').matches) {
        this.isOverflow = true
      } else {
        this.isOverflow = false
      }
    },
    addSubGroup() {
      this.$eventBus.$emit('open::addSubGroupList')
    },
    updateSubGroupCount(count) {
      this.subGroupCount = count
    },
    updateEtcMemberCount(count) {
      this.etcMemberCount = count
    },
  },
  mounted() {
    this.checkIsOverflow()
    this.$eventBus.$on('update::etcMemberCount', this.updateEtcMemberCount)
    this.$eventBus.$on('update::updateSubGroupCount', this.updateSubGroupCount)
    window.addEventListener('resize', this.checkIsOverflow)
  },
  beforeDestroy() {
    this.$eventBus.$off('update::etcMemberCount', this.updateEtcMemberCount)
    this.$eventBus.$off('update::updateSubGroupCount', this.updateSubGroupCount)
    window.removeEventListener('resize', this.checkIsOverflow)
  },
}
</script>

<style lang="scss">
.tab-workspace-sub-group {
  margin-top: 3.1429rem;
}

.tab-workspace-sub-group__header {
  position: relative;
  display: flex;
  align-items: center;
}

.tab-workspace-sub-group__header--title {
  margin-right: 1.1429rem;
  margin-bottom: 0.2857rem;
  color: rgb(30, 30, 30);
  font-weight: 500;
  font-size: 1.5714rem;
}

.tab-workspace-sub-group__header--description {
  color: rgb(122, 122, 122);
  font-weight: normal;
  font-size: 1.0714rem;
}

.tab-workspace-sub-group__header--etc {
  position: absolute;
  right: 8.5714rem;
  color: rgb(58, 74, 108);
  font-weight: 500;
  font-size: 1.0714rem;
}

.tab-workspace-sub-group__header--addgroup {
  position: absolute;
  right: 0;
  padding: 0.4286rem 1rem;
  color: rgb(117, 127, 145);

  font-weight: 500;
  font-size: 1.0714rem;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 4px;

  &.disabled {
    color: #bdc5cc;
    background-color: #fff;
    border: 1px solid #bdc5cc;

    &:hover {
      color: #bdc5cc;
      background-color: #fff;
      border: 1px solid #bdc5cc;
    }
  }
}
</style>
