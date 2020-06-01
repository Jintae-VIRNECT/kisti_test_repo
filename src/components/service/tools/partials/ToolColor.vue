<template>
  <div>
    <tool-button text="색상 선택" :active="status" @click.stop="clickHandler">
      <span class="line-color" :style="{ backgroundColor: pick }">{{
        pick
      }}</span>
    </tool-button>

    <transition name="tool">
      <div v-if="status" class="picker--container" @click.stop>
        <tool-picker
          type="list"
          :title="$t('service.controller_linecolor_label')"
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
          type="range"
          :title="$t('service.controller_opacity_label')"
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
    pick() {
      return this.tools.color
    },
    opacity() {
      return this.tools.opacity
    },
  },
  methods: {
    clickHandler() {
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
  width: 18px;
  height: 18px;
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
