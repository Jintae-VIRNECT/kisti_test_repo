<template>
  <div>
    <tool-button
      :text="$t('service.tool_color')"
      :active="status"
      @click.stop="clickHandler"
      :disabled="disabled"
      :disableTooltip="disableTooltip"
    >
      <span class="line-color" :style="{ backgroundColor: pick }">{{
        pick
      }}</span>
    </tool-button>

    <transition name="tool">
      <div v-if="status" class="picker--container" @click.stop>
        <tool-picker
          type="list"
          :title="$t('service.tool_color')"
          :list="colorchip"
          :current="pick"
          class="line_color"
        >
          <button
            slot="item"
            slot-scope="props"
            :style="{ backgroundColor: props.value }"
            @click.stop="selectColor(props.value)"
          >
            {{ props.value }}
          </button>
        </tool-picker>

        <tool-picker
          v-if="useOpacity"
          type="range"
          :title="$t('service.tool_opacity')"
          :step="20"
          :current="opacity * 100"
          :text="opacity * 100 + '%'"
          :change="changeOpacity"
        ></tool-picker>
      </div>
    </transition>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import { color } from 'utils/callOptions'
import { VIEW } from 'configs/view.config'

export default {
  name: 'ToolLineColor',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
      colorchip: color,
      showPicker: false,
    }
  },
  computed: {
    useOpacity() {
      return this.view === VIEW.DRAWING
    },
    pick() {
      return this.tools.color
    },
    opacity() {
      return this.tools.opacity
    },
  },
  methods: {
    clickHandler() {
      if (this.disabled) return
      const toStatus = !this.status
      this.$eventBus.$emit('control:close')
      this.$nextTick(() => {
        this.status = toStatus
      })
    },
    hidePicker() {
      this.status = false
    },
    selectColor(color) {
      const params = {
        target: 'drawColor',
        value: color,
      }
      this.setTool(params)
    },
    changeOpacity(opacity) {
      if (opacity === '0') opacity = '20'
      const params = {
        target: 'drawOpacity',
        value: opacity / 100,
      }
      this.setTool(params)
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
.line-color {
  z-index: 1;
  display: inline-block;
  width: 1.286rem;
  height: 1.286rem;
  margin: auto;
  overflow: hidden;
  text-indent: -99px;
  border-radius: 50%;
}

.tool-enter-active {
  transition: 0.3s transform ease;
}
.tool-enter {
  transform: translate(-50%, 10px);
}
</style>
