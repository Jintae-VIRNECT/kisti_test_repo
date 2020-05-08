<template>
  <div>
    <tool-button
      text="텍스트 크기 선택"
      :src="require('assets/image/ic-tool-txt-size.svg')"
      :active="status"
      @click.stop="clickHandler"
    >
    </tool-button>

    <transition name="tool">
      <div class="picker--container" v-if="status" @click.stop>
        <tool-picker
          type="list"
          :title="$t('service.controller_textsize_label')"
          :list="sizes"
          :current="pick"
          class="text_size"
        >
          <button
            slot="item"
            slot-scope="props"
            @click.stop="selectSize(props.value)"
            :style="{ fontSize: props.value + 'px' }"
          >
            {{ props.value }}
          </button>
        </tool-picker>
      </div>
    </transition>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import { size } from 'utils/toolOption'

export default {
  name: 'ToolTextSize',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
      sizes: size,
      showPicker: false,
    }
  },
  computed: {
    pick() {
      return this.tools.size
    },
  },
  methods: {
    clickHandler() {
      const toStatus = !this.status
      this.$eventBus.$emit('control:close')
      this.status = toStatus
    },
    hidePicker() {
      this.status = false
    },
    selectSize(value) {
      const params = {
        target: 'textSize',
        value,
      }
      console.log(params)
      this.setTool(params)

      this.$eventBus.$emit(`control:${this.callViewMode}:mode`, 'text')
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
