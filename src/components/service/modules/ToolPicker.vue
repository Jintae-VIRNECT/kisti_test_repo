<template>
  <div class="picker">
    <p class="picker--title">{{ title }}</p>

    <ul v-if="'list' === type" class="picker--list">
      <li
        v-for="item in list"
        :key="item"
        class="picker--item"
        :class="{ on: item === current }"
      >
        <slot name="item" :value="item"></slot>
      </li>
    </ul>

    <div v-else-if="'range' === type" class="picker--range" @click.stop>
      <div class="picker--range__box">
        <input
          type="range"
          :step="step"
          :min="min"
          :max="max"
          :value="current"
          class="picker--range__input"
          ref="rangeInput"
          @change="
            change($event.target.value)
            changeCurrent()
          "
        />
        <span class="picker--range__guage" :style="{ width: percent }"></span>
      </div>
      <label class="picker--range__text">{{ text }}</label>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ToolPicker',
  props: {
    type: {
      type: String,
      required: true,
      validator: function(value) {
        return ['list', 'range'].indexOf(value) >= 0
      },
    },
    title: {
      type: String,
      required: true,
    },
    list: Array,
    step: Number,
    current: [String, Number],
    text: [String, Number],
    min: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: 100,
    },
    change: Function,
  },
  computed: {
    percent() {
      return ((this.current - this.min) / (this.max - this.min)) * 100 + '%'
    },
  },
  methods: {
    changeCurrent() {
      this.$refs['rangeInput'].value = this.current
    },
  },
}
</script>
