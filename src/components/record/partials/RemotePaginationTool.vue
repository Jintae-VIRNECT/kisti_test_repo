<template>
  <nav class="pagination-tool">
    <ul>
      <li class="pagination-tool__link start"><a></a></li>
      <li class="pagination-tool__link page"><a>1</a></li>
      <li class="pagination-tool__link page"><a>2</a></li>
      <li class="pagination-tool__link page current"><a>3</a></li>
      <li class="pagination-tool__link page"><a>4</a></li>
      <li class="pagination-tool__link page"><a>5</a></li>
      <li class="pagination-tool__link end"><a></a></li>
    </ul>
  </nav>
</template>

<script>
export default {
  props: {
    totalPage: {
      type: Number,
      required: true,
    },
  },

  data() {
    return {
      currentPage: 1,
    }
  },

  mounted() {
    // if (this.totalPage < 4) {
    // }
  },
}
</script>

<style lang="scss">
// 스타일링 first
.pagination-tool {
  width: 268px;
  height: 38px;
  margin: auto;
  margin-top: 43px;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px rgb(234, 237, 243);
}

.pagination-tool__link {
  position: relative;
  display: inline-block;
  padding: 7px 15px;
  color: rgb(11, 31, 72);
  font-weight: normal;
  font-size: 14px;
  text-align: center;
  border-radius: 3px;

  &.start {
    width: 39px;
    padding: 6px 15px;
    background: url(~@/assets/img/ic_arrow_left.svg) center/60% no-repeat;
    transform: rotate(180deg);
  }

  &.start::after {
    position: absolute;
    top: -9px;
    left: -2px;
    height: 30px;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.end {
    width: 39px;
    padding: 6px 15px;
    background: url(~@/assets/img/ic_arrow_right.svg) center/60% no-repeat;
  }
  &.end::before {
    position: absolute;
    top: -9px;
    right: 36px;
    height: 30px;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.current {
    margin: 1px;
    padding: 5px 11px;
    color: #ffffff;
    font-weight: 500;
    background: #1665d8;
  }
}
</style>
