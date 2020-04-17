<template>
  <div class="widecard" :style="{ height: height + 'px' }" :class="customClass">
    <div class="card-item"><slot></slot></div>

    <div class="card-item"><slot name="column1"></slot></div>
    <div class="card-item"><slot name="column2"></slot></div>
    <div class="card-item"><slot name="column3"></slot></div>

    <div class="card-item widecard-tools">
      <slot name="column4"></slot>
      <popover
        v-if="menu"
        trigger="click"
        placement="bottom-start"
        popperClass="custom-popover"
      >
        <button slot="reference" class="widecard-tools__popover"></button>
        <div class="menu">
          <div class="menu--item">
            상세보기
          </div>
          <div class="menu--item">
            목록삭제
          </div>
        </div>
      </popover>
    </div>
  </div>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'WideCardExtend',
  components: {
    Popover,
  },
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
  },
  data() {
    return {}
  },
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.widecard {
  display: flex;
  flex-basis: 0px;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-bottom: 8px;
  padding: 22px 30px;
  background-color: #29292c;
  border-radius: 2px;

  &:hover {
    background-color: #313135;
  }
  .card-item {
    flex-grow: 1;
    flex-shrink: 1;
    flex-basis: 0;
  }
  .card-item:first-of-type {
    flex-grow: 0.8;
  }
  .card-item:last-of-type {
    flex-grow: 0.8;
  }
}

.widecard-tools {
  display: flex;
  align-items: center;
  justify-content: center;
}
.widecard-tools__popover {
  margin-left: 26px;
  width: 28px;
  height: 28px;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
  &:hover {
    background: url(~assets/image/back/mdpi_icn_more.svg) 50% no-repeat;
  }
}

.popover.custom-popover {
  background-color: #242427;
  border: 1px solid #979797;
  > .popover--body {
    padding: 0px;
    height: 92px;
    width: 120px;
  }
}

.menu {
  opacity: 70%;

  .menu--item {
    color: rgb(255, 255, 255);
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular;
    font-weight: normal;
    margin-top: 13px;
    margin-right: 42px;
    margin-bottom: 13px;
    margin-left: 20px;
  }
}
</style>
