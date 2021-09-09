<template>
  <div class="workspace-tab">
    <nav class="workspace-tab__nav">
      <ul class="flex offsetwidth"></ul>
    </nav>
    <template>
      <component :is="component"></component>
    </template>
  </div>
</template>

<script>
import WorkspaceSetting from '../../workspace/tab/WorkspaceSetting'

export default {
  name: 'WorkspaceTab',
  components: {
    setting: WorkspaceSetting,
  },
  data() {
    return {
      component: 'setting',
    }
  },
  computed: {
    tabComponents() {
      return [
        {
          name: 'setting',
          text: this.$t('workspace.setting'),
        },
      ]
    },
  },
  props: {
    fix: {
      type: [Number, Boolean],
      default: false,
    },
    inited: {
      type: Boolean,
      default: false,
    },
  },
  methods: {},
}
</script>
