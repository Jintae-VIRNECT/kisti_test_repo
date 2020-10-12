<template>
  <button
    type="button"
    class="local-record-button"
    :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }"
    @click="showList"
  >
    <img :src="imgSrc" />
    <p
      class="local-record-button--count"
      :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }"
    >
      {{ count }}건
    </p>
  </button>
</template>

<script>
export default {
  name: 'LocalRecordCountButton',
  data() {
    return {
      selected: false,
    }
  },
  props: {
    count: {
      type: Number,
      default: 0,
    },
    serialNum: {
      type: [Number, String],
      default: null,
    },
  },
  computed: {
    imgSrc() {
      if (this.count > 0 && this.selected) {
        return require('assets/image/ic_video_select.svg')
      } else if (this.count > 0) {
        return require('assets/image/ic_video_active.svg')
      } else {
        return require('assets/image/ic_video_default.svg')
      }
    },
  },
  methods: {
    showList() {
      if (this.count > 0) {
        this.$eventBus.$emit('open::record-list', this.serialNum)
        this.selected = !this.selected
      }
    },
    off() {
      this.selected = false
    },
  },
  mounted() {
    this.$eventBus.$on('close::record-list', this.off)
  },
  beforeDestroy() {
    this.$eventBus.$off('update:visible')
    this.$eventBus.$off('close::record-list')
  },
}
</script>

<style lang="scss">
.local-record-button {
  display: flex;
  align-items: center;
  justify-content: space-around;
  width: 5.8571rem;
  height: 2.8571rem;
  margin: 0 auto;
  padding: 0px 12px;
  color: #757f91;
  font-weight: 500;
  font-size: 1.0714rem;
  background: #f5f7fa;
  border-radius: 2px;
  &.nodata {
    background-color: #ffffff;
    border: 1px solid #edf0f4;
  }
  &.seleted {
    background: #1665d8;
  }
}

.local-record-button--count {
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.0714rem;
  &.nodata {
    color: #757f91;
  }
  &.seleted {
    color: #ffffff;
    font-size: 1.0714rem;
  }
}
</style>
