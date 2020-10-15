<template
  ><button
    type="button"
    class="count-button"
    :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }"
    @click="clickListener"
  >
    <img :src="imgSrc" />
    <p :class="{ nodata: count <= 0, normal: count > 0, seleted: selected }">
      {{ count }}건
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
  methods: {
    clickListener() {
      this.$listeners['click']()
      this.animateClass = this.animation
      setTimeout(() => {
        this.animateClass = ''
      }, 400)
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
