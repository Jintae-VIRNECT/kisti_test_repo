<template>
  <div class="workspace-tab">
    <nav class="workspace-tab__nav">
      <ul class="flex offsetwidth"></ul>
    </nav>
    <template>
      <component :is="component"></component>
      <button
        v-if="isMobileSize"
        class="guest-join btn"
        @click="joinAsGuest"
        v-html="joinText"
      ></button>
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
  props: {
    fix: {
      type: [Number, Boolean],
      default: false,
    },
    inited: {
      type: Boolean,
      default: false,
    },
    joinText: {
      type: String,
    },
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
  methods: {
    joinAsGuest() {
      this.$emit('joinAsGuest')
    },
  },
}
</script>
