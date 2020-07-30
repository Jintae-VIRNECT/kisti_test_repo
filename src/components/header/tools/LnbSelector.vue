<template>
  <popover
    trigger="click"
    placement="bottom-start"
    width="14rem"
    popperClass="header-lnb-selector"
  >
    <ul>
      <li
        class="header-lnb-selector__button"
        v-for="work of workspaceList"
        :key="work.uuid"
      >
        <button
          @click="changeSelect(work)"
          :class="{ active: work.uuid === workspace.uuid }"
        >
          <span
            class="header-lnb-selector__check"
            :class="{ active: work.uuid === workspace.uuid }"
          ></span>
          <p class="header-lnb-selector__title">{{ work.title }}</p>
          <!-- <p class="header-lnb-selector__description">
            워크스테이션 멤버: {{ option.member }}명
          </p> -->
        </button>
      </li>
    </ul>

    <button slot="reference" class="header-workspace-selector">
      {{ workspace.title }}
    </button>
  </popover>
</template>

<script>
import Popover from 'Popover'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'LnbSelector',
  components: {
    Popover,
  },
  data() {
    return {}
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
      })
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
