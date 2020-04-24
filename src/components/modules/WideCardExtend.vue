<template>
  <div class="widecard" :style="{ height: height + 'px' }" :class="customClass">
    <div class="card-item"><slot></slot></div>

    <div class="card-item"><slot name="column1"></slot></div>
    <div class="card-item"><slot name="column2"></slot></div>
    <div class="card-item"><slot name="column3"></slot></div>

    <div class="card-item widecard-tools">
      <slot name="column4"></slot>
      <slot name="column5"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WideCardExtend',
  components: {},
  props: {
    height: {
      type: Number,
      default: 86,
    },
    menu: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: Object,
      default: () => {
        return {}
      },
    },
    room: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.widecard {
  display: flex;
  flex-basis: 0px;
  align-items: center;
  justify-content: flex-start;

  min-width: 768px;
  margin-bottom: 8px;
  padding: 22px 30px;
  overflow: hidden;
  background-color: $color_darkgray_600;
  border-radius: 2px;

  &:hover {
    background-color: #313135;
  }
  .card-item {
    flex-basis: 0;
    flex-grow: 1;
    flex-shrink: 1;
  }
  .card-item:first-of-type {
    flex-grow: 0.8;
    min-width: 300px;
  }
  .card-item:nth-of-type(2) {
    flex-grow: 0.8;
  }

  .card-item:nth-last-child(2) {
    flex-grow: 0.7;
  }
  .card-item:last-of-type {
    flex-grow: 0.6;
  }
}

.widecard-tools {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>
