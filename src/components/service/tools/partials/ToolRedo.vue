<template>
  <tool-button
    :text="$t('service.tool_redo')"
    :active="status"
    :disabled="!isAvailable"
    :src="require('assets/image/ic-tool-redo.svg')"
    @click.stop="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW, ACTION } from 'configs/view.config'

export default {
  name: 'ToolRedo',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
      available: false,
    }
  },
  computed: {
    isAvailable() {
      if (this.disabled) {
        return false
      } else {
        return this.available
      }
    },
  },
  watch: {
    //협업보드에서 다른 탭 전환 되더라도 redo 버튼을 비활성화하지 않는다.
    // view() {
    //   this.available = false
    // },
    viewAction(val) {
      if ([ACTION.AR_POINTING, ACTION.AR_DRAWING].includes(val)) {
        this.available = false
      }
    },
  },
  methods: {
    clickHandler() {
      if (!this.isAvailable) return
      this.status = true
      let listener
      if (this.view === VIEW.DRAWING) {
        listener = VIEW.DRAWING
      } else {
        listener = this.viewAction
      }
      this.$eventBus.$emit(`control:${listener}:redo`)
      setTimeout(() => {
        this.status = false
      }, 100)
    },
    setAvailable(use) {
      this.available = use
    },
  },

  /* Lifecycling */
  created() {
    this.$eventBus.$on(`tool:redo`, this.setAvailable)
  },
  beforeDestroy() {
    this.$eventBus.$off(`tool:redo`, this.setAvailable)
  },
}
</script>
