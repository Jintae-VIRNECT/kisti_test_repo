<template
  ><button
    type="button"
    class="count-button"
    :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }"
    @click.stop="clickListener"
  >
    <img :src="imgSrc" />
    <p :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }">
      {{ $t('list.count', { count: count }) }}
    </p>
  </button>
</template>

<script>
export default {
  name: 'CountButton',
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
    images: {
      type: Object,
      require: true,
    },
    hover: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    imgSrc() {
      if (this.count > 0 && this.selected) {
        return this.images.select
      } else if (this.count > 0) {
        return this.images.active
      } else {
        return this.images.default
      }
    },
  },
  watch: {
    hover(after) {
      if (this.count > 0) {
        after ? (this.selected = true) : (this.selected = false)
      }
    },
  },
  methods: {
    clickListener() {
      if (this.count <= 0) return
      if (typeof this.$listeners['click'] === 'function') {
        this.$listeners['click']()
        this.animateClass = this.animation
        setTimeout(() => {
          this.animateClass = ''
        }, 400)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.count-button {
  display: flex;
  align-items: center;
  justify-content: space-around;
  width: 5.8571rem;
  height: 2.8571rem;
  margin: 0 auto;
  padding: 0px 0.8571rem;
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

  & > p {
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
}
</style>
