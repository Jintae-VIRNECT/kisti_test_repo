<template>
  <popover
    trigger="click"
    placement="bottom-start"
    :width="0"
    popperClass="header-lnb-selector"
    @visible="toggleActive"
  >
    <vue2-scrollbar>
      <ul class="header-lnb-selector__layer">
        <li
          class="header-lnb-selector__button"
          v-for="work of workspaceList"
          :key="work.uuid"
        >
          <button
            @click="changeSelect(work)"
            :class="{ active: work.uuid === workspace.uuid }"
          >
            <profile
              class="header-lnb-selector__profile"
              :group="true"
              :image="work.profile"
              :thumbStyle="{ width: '3rem', height: '3rem' }"
            ></profile>
            <span
              class="header-lnb-selector__check"
              :class="{ active: work.uuid === workspace.uuid }"
            ></span>
            <p class="header-lnb-selector__title">{{ work.title }}</p>
            <!-- <p class="header-lnb-selector__description">
              워크스테이션 멤버: {{ option.member }}명
            </p> -->
            <span
              class="header-lnb-selector__mobilecheck"
              :class="{ active: work.uuid === workspace.uuid }"
            ></span>
          </button>
        </li>
      </ul>
    </vue2-scrollbar>

    <button
      slot="reference"
      class="header-workspace-selector"
      :class="{ selected: workspaceSelectorFocused }"
    >
      {{ workspace.title }}
    </button>
  </popover>
</template>

<script>
import Popover from 'Popover'
import Profile from 'Profile'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'LnbSelector',
  components: {
    Popover,
    Profile,
  },
  data() {
    return {
      popoverWidth: '14rem',
      workspaceSelectorFocused: false,
    }
  },
  computed: {
    ...mapGetters(['workspaceList']),
  },
  methods: {
    ...mapActions(['changeWorkspace']),
    changeSelect(workspace) {
      this.changeWorkspace(workspace)
      this.$push.changeSubscribe(workspace)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
        this.popoverWidth = this.$el.querySelector(
          '.header-workspace-selector',
        ).offsetWidth
      })
    },

    //워크 스페이스 변경 후 변경실패 시 롤백해야하는 경우를 위해 추가한 메서드
    updateWorkspace(workspaceId) {
      const workspace = this.workspaceList.find(
        workspace => workspace.uuid === workspaceId,
      )
      this.changeSelect(workspace)
    },

    toggleActive(visible) {
      this.workspaceSelectorFocused = visible
    },
  },

  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('workspaceChange', this.updateWorkspace)
  },
  beforeDestroy() {
    this.$eventBus.$off('workspaceChange')
  },
}
</script>
