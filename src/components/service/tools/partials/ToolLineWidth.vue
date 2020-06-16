<template>
  <div>
    <tool-button
      text="선 두께"
      :active="status"
      :disabled="disabled"
      :src="require('assets/image/ic-tool-line-weight.svg')"
      @click.stop="clickHandler()"
    >
    </tool-button>

    <transition name="tool">
      <div v-if="status" class="picker--container" @click.stop>
        <tool-picker
          type="list"
          :title="$t('service.controller_linesize_label')"
          :list="sizes"
          :current="pick"
          class="line_width"
        >
          <button
            slot="item"
            slot-scope="props"
            @click.stop="selectSize(props.value)"
          >
            <span class="line" :style="{ height: props.value + 'px' }"></span>
            {{ `${props.value}px` }}
          </button>
        </tool-picker>
      </div>
    </transition>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import { width } from 'utils/callOptions'

export default {
  name: 'ToolLineWidth',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
      sizes: width,
      showPicker: false,
    }
  },
  computed: {
    pick() {
      return this.tools.lineWidth
    },
  },
  methods: {
    selectSize(value) {
      const params = {
        target: 'lineWidth',
        value,
      }
      this.setTool(params)
    },
    clickHandler() {
      const toStatus = !this.status
      this.$eventBus.$emit('control:close')
      this.status = toStatus
    },
    hidePicker() {
      this.status = false
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('control:close', this.hidePicker)
    window.addEventListener('click', this.hidePicker)
  },
  beforeDestroy() {
    this.$eventBus.$off('control:close', this.hidePicker)
    window.removeEventListener('click', this.hidePicker)
  },
}
</script>

<style scoped>
.tool-enter-active {
  transition: 0.3s transform ease;
}
.tool-enter {
  transform: translate(-50%, 10px);
}
</style>
