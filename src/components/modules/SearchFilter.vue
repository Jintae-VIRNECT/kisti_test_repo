<template>
  <div class="search-filter">
    <div class="search-filter__input">
      <input type="text" placeholder="회원명 검색" />
    </div>
    <button class="search-filter__sort">정렬</button>
    <popover
      class="search-filter__select"
      placement="bottom"
      trigger="click"
      :popperClass="'sort'"
      :width="150"
    >
      <button slot="reference" class="sort-select">이름 순</button>
      <div class="sort-options">
        <button class="sort-options__button">이름 순</button>
        <button class="sort-options__button">아이디 순</button>
        <button class="sort-options__button">내맘대로 정렬</button>
      </div>
    </popover>
  </div>
</template>

<script>
import Popover from 'Popover'
export default {
  name: 'SearchFilter',
  components: {
    Popover,
  },
  props: {},
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
@import '~assets/style/mixin';
.search-filter {
  display: flex;
}
.search-filter__input {
  position: relative;
  // width: 186px;
  height: 36px;
  padding: 0 36px 0 16px;
  // background: url('~assets/image/material_search.png') 50% 12px/22px no-repeat;
  background-color: transparent;
  border: solid 1px rgba(#b3b3b3, 0.88);
  border-radius: 18px;
  opacity: 0.2;
  &:before {
    position: absolute;
    top: 6px;
    right: 10px;
    width: 22px;
    height: 22px;
    background: url(~assets/image/material_search.png) 50%/22px no-repeat;
    // background-color: #eeee00;
    content: '';
  }
  > input {
    width: 140px;
    height: 100%;
    color: #fff;
    background-color: transparent;
    border: none;
    transition: width 0.25s ease;
    &:focus {
      width: 230px;
      transition: width 0.25s ease;
    }
  }
}
.search-filter__sort {
  @include ir();
  width: 30px;
  height: 36px;
  background: url(~assets/image/material_swap_vert.png) 50%/22px no-repeat;
}

.search-filter__select {
  .sort-select {
    position: relative;
    padding: 6px 34px 6px 0;
    color: rgba(#d2d2d2, 0.9);
    font-size: 15px;
    &:after {
      position: absolute;
      top: 7px;
      right: 7px;
      width: 22px;
      height: 22px;
      background: url('~assets/image/ic-select-collapse.svg') 50%/22px no-repeat;
      // background-color: #eeee00;
      content: '';
    }
  }
}
.popover.sort {
  width: 150px;
  min-width: 150px;
  > .popover--body {
    padding: 0;
  }
  .sort-options {
    padding: 0;
    border: solid 1px #434346;
  }
  .sort-options__button {
    width: 100%;
    padding: 5px 0;
    color: #fff;
    background-color: #1d1d1f;
    border-top: solid 1px #434346;
    &:first-child {
      border-top: none;
    }
    &:hover {
      background-color: #434346;
    }
  }
}
</style>
